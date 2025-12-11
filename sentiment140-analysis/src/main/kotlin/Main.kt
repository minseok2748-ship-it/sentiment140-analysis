import java.io.File
import kotlin.system.measureTimeMillis

fun safeSplitCsv(line: String): List<String> {
    // CSV에서 큰따옴표 안의 쉼표는 분리하지 않도록 하는 정규식
    val regex = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")
    return line.split(regex)
}

fun normalizeText(s: String): String {
    return s.trim().trim('"')
}

fun tokenize(text: String): Sequence<String> {
    val cleaned = text.lowercase().replace(Regex("[^a-z0-9@# ]"), " ")
    return cleaned.split(Regex("\\s+")).asSequence().filter { it.isNotBlank() }
}

val STOPWORDS = setOf(
    "the","and","to","a","i","it","is","in","you","of","for","on","my","me","that","this",
    "with","not","was","are","they","we","be","have","has","so","but","if","at","from","as",
    "he","she","his","her","them","just","do","don","t","your","what","or","an","by","all",
    "can","like","get","about","would","there","up","out","when","who","one","no","will","how"
)

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: ./gradlew run --args=\"/absolute/path/to/training.csv\"")
        return
    }
    val path = args[0]
    val file = File(path)
    if (!file.exists()) {
        println("File not found: $path")
        return
    }

    println("Starting analysis for file: $path")

    val timeMs = measureTimeMillis {
        var total = 0L
        var posCount = 0L
        var negCount = 0L
        var posLen = 0L
        var negLen = 0L

        val posWords = mutableMapOf<String, Int>()
        val negWords = mutableMapOf<String, Int>()
        val userCounts = mutableMapOf<String, Int>()

        file.useLines { lines ->
            val it = lines.iterator()
            while (it.hasNext()) {
                val line = it.next()

                // 혹시 header가 있는 경우 스킵
                if (total == 0L && line.lowercase().startsWith("sentiment")) {
                    total++
                    continue
                }

                val cols = safeSplitCsv(line)
                if (cols.size < 6) {
                    total++
                    continue
                }

                val label = normalizeText(cols[0])
                val user = normalizeText(cols[4])
                val text = normalizeText(cols[5])

                // 사용자별 등장 횟수
                userCounts[user] = userCounts.getOrDefault(user, 0) + 1

                when (label) {
                    "4" -> {
                        posCount++
                        posLen += text.length
                        for (tok in tokenize(text)) {
                            val w = tok.replace(Regex("^[^a-z0-9@#]+|[^a-z0-9@#]+$"), "")
                            if (w.isEmpty()) continue
                            if (w in STOPWORDS) continue
                            posWords[w] = posWords.getOrDefault(w, 0) + 1
                        }
                    }
                    "0" -> {
                        negCount++
                        negLen += text.length
                        for (tok in tokenize(text)) {
                            val w = tok.replace(Regex("^[^a-z0-9@#]+|[^a-z0-9@#]+$"), "")
                            if (w.isEmpty()) continue
                            if (w in STOPWORDS) continue
                            negWords[w] = negWords.getOrDefault(w, 0) + 1
                        }
                    }
                }

                total++
                if (total % 200000 == 0L) {
                    println("Processed $total lines")
                }
            }
        }

        val totalLabels = posCount + negCount

        println("=== Summary ===")
        println("Total lines processed: $total")
        println("Positive (4): $posCount")
        println("Negative (0): $negCount")
        if (totalLabels > 0) {
            println("Positive %: ${"%.2f".format(posCount * 100.0 / totalLabels)}")
            println("Negative %: ${"%.2f".format(negCount * 100.0 / totalLabels)}")
        }

        if (posCount > 0) println("Positive avg length: ${"%.2f".format(posLen.toDouble() / posCount)}")
        if (negCount > 0) println("Negative avg length: ${"%.2f".format(negLen.toDouble() / negCount)}")

        fun topN(map: Map<String, Int>, n: Int) =
            map.entries.sortedByDescending { it.value }.take(n).map { "${it.key} (${it.value})" }

        println("\nTop 20 positive words:")
        topN(posWords, 20).forEach { println(it) }

        println("\nTop 20 negative words:")
        topN(negWords, 20).forEach { println(it) }

        println("\nTop 10 users by tweet count:")
        userCounts.entries.sortedByDescending { it.value }.take(10).forEach {
            println("${it.key} (${it.value})")
        }

        // === 결과를 analysis.md로 자동 저장 ===
        val analysisFile = File("analysis.md")

        val sb = StringBuilder()
        sb.appendLine("# Analysis Summary")
        sb.appendLine()
        sb.appendLine("File analyzed: $path")
        sb.appendLine()
        sb.appendLine("Total lines processed: $total")
        sb.appendLine()
        sb.appendLine("## Label distribution")
        sb.appendLine("- Positive: $posCount")
        sb.appendLine("- Negative: $negCount")
        sb.appendLine()
        sb.appendLine("## Average tweet length")
        sb.appendLine("- Positive avg length: ${"%.2f".format(posLen.toDouble() / posCount)}")
        sb.appendLine("- Negative avg length: ${"%.2f".format(negLen.toDouble() / negCount)}")
        sb.appendLine()
        sb.appendLine("## Top 20 positive words")
        topN(posWords, 20).forEach { sb.appendLine("- $it") }
        sb.appendLine()
        sb.appendLine("## Top 20 negative words")
        topN(negWords, 20).forEach { sb.appendLine("- $it") }
        sb.appendLine()
        sb.appendLine("## Top 10 users")
        userCounts.entries.sortedByDescending { it.value }.take(10).forEach {
            sb.appendLine("- ${it.key} (${it.value})")
        }

        analysisFile.writeText(sb.toString())
        println("\nWrote analysis.md")
    }

    println("Done in ${timeMs / 1000.0} seconds.")
}
