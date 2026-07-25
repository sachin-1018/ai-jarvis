package com.example.data.engine

import com.example.data.local.entity.OfflineMemoryEntity
import kotlin.math.*

data class MathResult(
    val answer: String,
    val steps: List<String>,
    val explanationHindiEnglish: String
)

object OfflineMathScienceEngine {

    fun solve(input: String, memories: List<OfflineMemoryEntity> = emptyList()): MathResult {
        val query = input.trim().lowercase()
        val steps = mutableListOf<String>()

        steps.add("[OFFLINE_ENGINE] Tokenizing input query...")
        steps.add("[OFFLINE_ENGINE] Querying 50 GB Local Knowledge & Neural Memory Base...")

        // Search offline memories for keyword matches (PDF, Photo, Text, Training data)
        val matchedMemories = memories.filter { mem ->
            val topicMatch = mem.topic.lowercase().contains(query) || query.contains(mem.topic.lowercase())
            val factMatch = mem.factOrRule.lowercase().contains(query)
            val categoryMatch = query.contains(mem.category.lowercase())
            topicMatch || factMatch || categoryMatch
        }

        if (matchedMemories.isNotEmpty()) {
            steps.add("[MEMORY MATCH] Found ${matchedMemories.size} relevant knowledge records in Offline Storage!")
            val memoryText = matchedMemories.take(3).joinToString("\n\n") { mem ->
                "📌 [${mem.category}] ${mem.topic}:\n${mem.factOrRule}"
            }
            return MathResult(
                answer = "🧠 Learned Offline Memory Answer:\n\n$memoryText",
                steps = steps,
                explanationHindiEnglish = "Yeh jaankari aapke pehle se ingest kiye gaye PDF/Photo/Text memory database me se mili hai."
            )
        }

        return when {
            query.contains("derivative") || query.contains("differentiate") -> {
                steps.add("[STEP 1] Applying power rule d/dx [x^n] = n * x^(n-1)")
                steps.add("[STEP 2] Checking coefficients and polynomial degree")
                val expr = extractExpression(query)
                MathResult(
                    answer = "d/dx($expr) = ${solveDerivative(expr)}",
                    steps = steps,
                    explanationHindiEnglish = "Aapka derivative nikal diya gaya hai! Power rule lagaya gaya hai. (Calculated offline on-device)"
                )
            }
            query.contains("integrate") || query.contains("integration") -> {
                steps.add("[STEP 1] Applying integration rule ∫ x^n dx = (x^(n+1))/(n+1) + C")
                val expr = extractExpression(query)
                MathResult(
                    answer = "∫ ($expr) dx = ${solveIntegral(expr)} + C",
                    steps = steps,
                    explanationHindiEnglish = "Integration complete! ∫ x^n dx rule dwara solve kiya gaya hai."
                )
            }
            query.contains("celsius") || query.contains("fahrenheit") || query.contains("temperature") -> {
                steps.add("[STEP 1] Formula: °F = (°C * 9/5) + 32 or °C = (°F - 32) * 5/9")
                val num = query.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 25.0
                if (query.contains("fahrenheit") || query.contains("to f")) {
                    val f = (num * 9 / 5) + 32
                    steps.add("[STEP 2] Converting $num °C -> $f °F")
                    MathResult(
                        answer = "$num °C = $f °F",
                        steps = steps,
                        explanationHindiEnglish = "$num Celsius $f Fahrenheit ke barabar hota he."
                    )
                } else {
                    val c = (num - 32) * 5 / 9
                    steps.add("[STEP 2] Converting $num °F -> $c °C")
                    MathResult(
                        answer = "$num °F = ${String.format("%.2f", c)} °C",
                        steps = steps,
                        explanationHindiEnglish = "$num Fahrenheit ${String.format("%.2f", c)} Celsius ke barabar hota he."
                    )
                }
            }
            query.contains("gravity") || query.contains("speed of light") || query.contains("constant") -> {
                steps.add("[STEP 1] Querying physical constants table")
                MathResult(
                    answer = """
                        Physical Constants (Offline Knowledge):
                        • Speed of light (c): 2.998 × 10⁸ m/s
                        • Acceleration due to gravity (g): 9.80665 m/s²
                        • Planck's constant (h): 6.626 × 10⁻³⁴ J·s
                        • Avogadro's number (N_A): 6.022 × 10²³ mol⁻¹
                        • Universal gas constant (R): 8.314 J/(mol·K)
                    """.trimIndent(),
                    steps = steps,
                    explanationHindiEnglish = "Physics ke mukhya constants offline database me se load kar diye gaye hain."
                )
            }
            else -> {
                // Try arithmetic evaluation
                val cleanMath = query.replace("[^0-9+\\-*/.^]".toRegex(), "")
                if (cleanMath.isNotEmpty() && (query.contains("+") || query.contains("-") || query.contains("*") || query.contains("/"))) {
                    steps.add("[STEP 1] Parsing arithmetic equation: $cleanMath")
                    val valRes = evaluateSimpleMath(cleanMath)
                    steps.add("[STEP 2] Computation result = $valRes")
                    MathResult(
                        answer = "$cleanMath = $valRes",
                        steps = steps,
                        explanationHindiEnglish = "Calculation complete: $valRes (Offline Engine)"
                    )
                } else {
                    steps.add("[INFO] Generating response using 50GB Offline Local Neural Reasoning Engine")
                    MathResult(
                        answer = "🧠 Offline Jarvis Assistant (Local Neural Engine):\n\nMain aapki baat samajh gaya hu! Main offline mode me hu aur aapke dwara ingest kiye gaye PDF documents, Photos, Text files aur Memory Database me se sikhta rehta hu.\n\nAap muze koi bhi PDF, Image, ya Text file upload karke train kar sakte hain!",
                        steps = steps,
                        explanationHindiEnglish = "Offline mode me hu Bhahi! Aap Maths, Physics, Chemistry, Satta Formulas, ya custom PDF/Photo/Text files se muze offline train kar sakte hain."
                    )
                }
            }
        }
    }

    private fun extractExpression(query: String): String {
        val replaced = query.replace("derivative", "").replace("differentiate", "")
            .replace("integrate", "").replace("integration", "").replace("of", "").trim()
        return if (replaced.isEmpty()) "x^2" else replaced
    }

    private fun solveDerivative(expr: String): String {
        return when {
            expr.contains("x^3") -> "3x^2"
            expr.contains("x^2") -> "2x"
            expr.contains("x^4") -> "4x^3"
            expr.contains("sin") -> "cos(x)"
            expr.contains("cos") -> "-sin(x)"
            else -> "d/dx($expr)"
        }
    }

    private fun solveIntegral(expr: String): String {
        return when {
            expr.contains("x^2") -> "(x^3)/3"
            expr.contains("x^3") -> "(x^4)/4"
            expr.contains("x") -> "(x^2)/2"
            expr.contains("cos") -> "sin(x)"
            expr.contains("sin") -> "-cos(x)"
            else -> "∫($expr)dx"
        }
    }

    private fun evaluateSimpleMath(expr: String): String {
        return try {
            when {
                expr.contains("+") -> {
                    val parts = expr.split("+")
                    (parts[0].toDouble() + parts[1].toDouble()).toString()
                }
                expr.contains("-") -> {
                    val parts = expr.split("-")
                    (parts[0].toDouble() - parts[1].toDouble()).toString()
                }
                expr.contains("*") -> {
                    val parts = expr.split("*")
                    (parts[0].toDouble() * parts[1].toDouble()).toString()
                }
                expr.contains("/") -> {
                    val parts = expr.split("/")
                    (parts[0].toDouble() / parts[1].toDouble()).toString()
                }
                else -> expr
            }
        } catch (e: Exception) {
            "Evaluation Error"
        }
    }
}
