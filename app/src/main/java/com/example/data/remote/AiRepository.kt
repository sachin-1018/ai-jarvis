package com.example.data.remote

import android.content.Context
import com.example.BuildConfig
import com.example.data.engine.OfflineMathScienceEngine
import com.example.data.local.dao.ApiProviderDao
import com.example.data.local.entity.ApiProviderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiResponse(
    val replyText: String,
    val providerUsed: String,
    val modelUsed: String,
    val isOffline: Boolean = false,
    val thinkingSteps: List<String> = emptyList()
)

class AiRepository(
    private val context: Context,
    private val apiProviderDao: ApiProviderDao
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        prompt: String,
        imageUri: String? = null,
        memories: List<com.example.data.local.entity.OfflineMemoryEntity> = emptyList()
    ): AiResponse = withContext(Dispatchers.IO) {

        val selectedProvider = apiProviderDao.getSelectedProvider()
            ?: apiProviderDao.getProviderByKey("gemini")
            ?: ApiProviderEntity(
                providerKey = "gemini",
                providerName = "Google Gemini",
                apiKey = "",
                baseUrl = "https://generativelanguage.googleapis.com/",
                selectedModel = "gemini-2.0-flash",
                availableModels = "gemini-1.5-pro, gemini-1.5-flash, gemini-2.0-flash, gemini-2.0-flash-lite",
                systemPrompt = "You are AI Jarvis created by Sachin Solunke."
            )

        val apiKey = selectedProvider.apiKey.ifEmpty {
            if (selectedProvider.providerKey == "gemini") BuildConfig.GEMINI_API_KEY else ""
        }

        // Check if phone control or math offline query
        if (apiKey.isEmpty() && selectedProvider.providerKey != "gemini") {
            val mathRes = OfflineMathScienceEngine.solve(prompt, memories)
            return@withContext AiResponse(
                replyText = "${mathRes.answer}\n\n[Hindi/English Note]: ${mathRes.explanationHindiEnglish}\n\n(Tip: Enter API key in Provider Settings to activate ${selectedProvider.providerName} online mode!)",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "On-Device Neural Heuristics",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        }

        return@withContext when (selectedProvider.providerKey) {
            "gemini" -> callGeminiApi(selectedProvider, apiKey, prompt, memories)
            "anthropic" -> callAnthropicApi(selectedProvider, apiKey, prompt)
            "cohere" -> callCohereApi(selectedProvider, apiKey, prompt)
            else -> callOpenAiCompatibleApi(selectedProvider, apiKey, prompt)
        }
    }

    private fun callGeminiApi(provider: ApiProviderEntity, apiKey: String, prompt: String, memories: List<com.example.data.local.entity.OfflineMemoryEntity> = emptyList()): AiResponse {
        val effectiveKey = apiKey.trim().ifEmpty { BuildConfig.GEMINI_API_KEY.trim() }
        if (effectiveKey.isEmpty() || effectiveKey == "MY_GEMINI_API_KEY") {
            val offlineRes = OfflineMathScienceEngine.solve(prompt, memories)
            return AiResponse(
                replyText = "${offlineRes.answer}\n\n${offlineRes.explanationHindiEnglish}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "On-Device Math/Science Solver",
                isOffline = true,
                thinkingSteps = offlineRes.steps
            )
        }

        return try {
            val modelName = provider.selectedModel.trim().ifEmpty { "gemini-2.0-flash" }
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$effectiveKey"

            val sysInstruction = if (provider.systemPrompt.isNotEmpty()) {
                JSONObject().put("parts", JSONArray().put(JSONObject().put("text", provider.systemPrompt)))
            } else {
                JSONObject().put("parts", JSONArray().put(JSONObject().put("text", "You are AI Jarvis, a bilingual assistant (Hindi/English) created by Sachin Solunke.")))
            }

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().put(
                    JSONObject().put("parts", JSONArray().put(
                        JSONObject().put("text", prompt)
                    ))
                ))
                put("systemInstruction", sysInstruction)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = try { JSONObject(responseStr) } catch (_: Exception) { null }
                val text = jsonRes?.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")

                if (!text.isNullOrEmpty()) {
                    return AiResponse(
                        replyText = text,
                        providerUsed = provider.providerName,
                        modelUsed = modelName,
                        isOffline = false
                    )
                }
            }

            val errorMsg = try {
                val jsonErr = JSONObject(responseStr)
                jsonErr.optJSONObject("error")?.optString("message")
                    ?: jsonErr.optJSONObject("error")?.optString("status")
                    ?: "HTTP ${response.code}"
            } catch (_: Exception) {
                "HTTP ${response.code}"
            }

            val mathRes = OfflineMathScienceEngine.solve(prompt, memories)
            AiResponse(
                replyText = "⚠️ Gemini API Error ($errorMsg)\n\nPlease check your API Key in Provider Settings.\n\nOffline Jarvis Engine Response:\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "On-Device Neural Solver",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        } catch (e: Exception) {
            val mathRes = OfflineMathScienceEngine.solve(prompt, memories)
            val cleanErr = e.localizedMessage ?: "Network error"
            AiResponse(
                replyText = "Offline / Connection Notice ($cleanErr):\n\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "On-Device Engine",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        }
    }

    private fun callOpenAiCompatibleApi(provider: ApiProviderEntity, apiKey: String, prompt: String): AiResponse {
        val trimmedKey = apiKey.trim()
        val baseUrl = if (provider.baseUrl.trim().endsWith("/")) provider.baseUrl.trim() else "${provider.baseUrl.trim()}/"
        val url = "${baseUrl}chat/completions"

        return try {
            val jsonBody = JSONObject().apply {
                put("model", provider.selectedModel.trim().ifEmpty { "big-pickle" })
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", provider.systemPrompt.ifEmpty { "You are AI Jarvis, created by Sachin Solunke." })
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            val reqBuilder = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))

            if (trimmedKey.isNotEmpty()) {
                reqBuilder.addHeader("Authorization", "Bearer $trimmedKey")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = try { JSONObject(responseStr) } catch (_: Exception) { null }
                val choices = jsonRes?.optJSONArray("choices")
                val firstChoice = choices?.optJSONObject(0)
                val messageObj = firstChoice?.optJSONObject("message")
                val content = messageObj?.optString("content")

                if (!content.isNullOrEmpty()) {
                    return AiResponse(
                        replyText = content,
                        providerUsed = provider.providerName,
                        modelUsed = provider.selectedModel,
                        isOffline = false
                    )
                }
            }

            val errorMsg = try {
                val jsonErr = JSONObject(responseStr)
                jsonErr.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
            } catch (_: Exception) {
                "HTTP ${response.code}"
            }

            val mathRes = OfflineMathScienceEngine.solve(prompt)
            AiResponse(
                replyText = "⚠️ ${provider.providerName} Error ($errorMsg)\n\nPlease check API Key in Settings.\n\nOffline Jarvis Engine Response:\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "On-Device Engine",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        } catch (e: Exception) {
            val mathRes = OfflineMathScienceEngine.solve(prompt)
            val cleanErr = e.localizedMessage ?: "Network error"
            AiResponse(
                replyText = "Offline / Connection Notice ($cleanErr):\n\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "Local Solver",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        }
    }

    private fun callAnthropicApi(provider: ApiProviderEntity, apiKey: String, prompt: String): AiResponse {
        val url = "https://api.anthropic.com/v1/messages"

        return try {
            val jsonBody = JSONObject().apply {
                put("model", provider.selectedModel)
                put("max_tokens", 1024)
                put("system", provider.systemPrompt.ifEmpty { "You are AI Jarvis." })
                put("messages", JSONArray().put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    }
                ))
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = JSONObject(responseStr)
                val contentArr = jsonRes.optJSONArray("content")
                val text = contentArr?.optJSONObject(0)?.optString("text") ?: "Claude response received."

                AiResponse(
                    replyText = text,
                    providerUsed = provider.providerName,
                    modelUsed = provider.selectedModel,
                    isOffline = false
                )
            } else {
                val mathRes = OfflineMathScienceEngine.solve(prompt)
                AiResponse(
                    replyText = "Claude API Error (${response.code}).\n\n${mathRes.answer}",
                    providerUsed = "Jarvis Offline Fallback",
                    modelUsed = "Offline Engine",
                    isOffline = true,
                    thinkingSteps = mathRes.steps
                )
            }
        } catch (e: Exception) {
            val mathRes = OfflineMathScienceEngine.solve(prompt)
            AiResponse(
                replyText = "Offline / Error: ${e.localizedMessage}\n\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "Offline Engine",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        }
    }

    private fun callCohereApi(provider: ApiProviderEntity, apiKey: String, prompt: String): AiResponse {
        val url = "https://api.cohere.ai/v1/chat"

        return try {
            val jsonBody = JSONObject().apply {
                put("message", prompt)
                put("model", provider.selectedModel)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val jsonRes = JSONObject(responseStr)
                val text = jsonRes.optString("text", "Cohere response received.")

                AiResponse(
                    replyText = text,
                    providerUsed = provider.providerName,
                    modelUsed = provider.selectedModel,
                    isOffline = false
                )
            } else {
                val mathRes = OfflineMathScienceEngine.solve(prompt)
                AiResponse(
                    replyText = "Cohere API Error (${response.code}).\n\n${mathRes.answer}",
                    providerUsed = "Jarvis Offline Fallback",
                    modelUsed = "Offline Engine",
                    isOffline = true,
                    thinkingSteps = mathRes.steps
                )
            }
        } catch (e: Exception) {
            val mathRes = OfflineMathScienceEngine.solve(prompt)
            AiResponse(
                replyText = "Offline / Error: ${e.localizedMessage}\n\n${mathRes.answer}",
                providerUsed = "Jarvis Offline Engine",
                modelUsed = "Offline Engine",
                isOffline = true,
                thinkingSteps = mathRes.steps
            )
        }
    }
}
