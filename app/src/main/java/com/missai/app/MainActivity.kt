
package com.missai.app

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var scrollView: ScrollView

    private val prefsName = "miss_ai_preferences"
    private val historyKey = "chat_history"

    private val purple = Color.rgb(116, 75, 220)
    private val backgroundColor = Color.rgb(18, 15, 30)
    private val assistantBubble = Color.rgb(43, 35, 62)
    private val userBubble = Color.rgb(91, 60, 160)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createScreen()
        loadHistory()

        if (messages.childCount == 0) {
            addMessage(
                "assistant",
                "नमस्ते! मैं MISS AI हूँ। बताओ, मैं तुम्हारी क्या मदद कर सकती हूँ?"
            )
        }
    }

    private fun createScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 20, 16, 20)
            setBackgroundColor(Color.rgb(29, 22, 45))
        }

        val title = TextView(this).apply {
            text = "MISS AI"
            textSize = 25f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Your personal AI companion"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        header.addView(title)
        header.addView(subtitle)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        scrollView = ScrollView(this).apply {
            isFillViewport = true
        }

        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 16, 12, 16)
        }

        scrollView.addView(messages)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 10, 10, 10)
            setBackgroundColor(Color.rgb(29, 22, 45))
        }

        input = EditText(this).apply {
            hint = "Message MISS AI..."
            setHintTextColor(Color.LTGRAY)
            setTextColor(Color.WHITE)
            textSize = 15f
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_SEND
            setPadding(14, 10, 14, 10)
            background = rounded(
                Color.rgb(48, 39, 65),
                24
            )
        }

        val sendButton = Button(this).apply {
            text = "Send"
            textSize = 14f
            setTextColor(Color.WHITE)
            background = rounded(purple, 24)
            isAllCaps = false
            setOnClickListener {
                sendMessage()
            }
        }

        bottom.addView(
            input,
            LinearLayout.LayoutParams(0, 52, 1f).apply {
                marginEnd = 8
            }
        )

        bottom.addView(
            sendButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                52
            )
        )

        root.addView(bottom)

        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        setContentView(root)
    }

    private fun rounded(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    private fun sendMessage() {

        val message = input.text.toString().trim()

        if (message.isEmpty()) return

        addMessage("user", message)
        input.text.clear()

        val reply = generateReply(message)

        messages.postDelayed({
            addMessage("assistant", reply)
        }, 350)
    }

    private fun addMessage(
        role: String,
        text: String
    ) {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (role == "user") {
                Gravity.END
            } else {
                Gravity.START
            }
            setPadding(4, 5, 4, 5)
        }

        val bubble = TextView(this).apply {
            this.text = text
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(15, 11, 15, 11)
            background = rounded(
                if (role == "user") userBubble
                else assistantBubble,
                18
            )
        }

        row.addView(
            bubble,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.85f
            )
        )

        if (role == "user") {
            row.gravity = Gravity.END
        } else {
            row.gravity = Gravity.START
        }

        messages.addView(row)

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        saveHistory()
    }

    private fun generateReply(message: String): String {

        val text = message.lowercase()

        return when {

            text.contains("hello") ||
            text.contains("hi") ||
            text.contains("नमस्ते") ||
            text.contains("हेलो") -> {
                "नमस्ते! 😊 बताओ, आज क्या बात करनी है?"
            }

            text.contains("your name") ||
            text.contains("तुम्हारा नाम") -> {
                "मेरा नाम MISS AI है। मैं तुम्हारी personal assistant हूँ।"
            }

            text.contains("how are you") ||
            text.contains("कैसी हो") -> {
                "मैं ठीक हूँ! तुम बताओ, कैसे हो?"
            }

            text.contains("time") ||
            text.contains("समय") -> {
                "अभी का समय: ${
                    java.text.SimpleDateFormat(
                        "hh:mm a",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date())
                }"
            }

            text.contains("date") ||
            text.contains("तारीख") -> {
                "आज की तारीख: ${
                    java.text.SimpleDateFormat(
                        "dd MMMM yyyy",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date())
                }"
            }

            text.contains("thank") ||
            text.contains("धन्यवाद") ||
            text.contains("शुक्रिया") -> {
                "तुम्हारा स्वागत है! ❤️"
            }

            text.contains("bye") ||
            text.contains("बाय") -> {
                "फिर मिलेंगे! अपना ख्याल रखना।"
            }

            else -> {
                "मैंने तुम्हारी बात समझने की कोशिश की। अभी मेरी offline intelligence सीमित है। असली AI responses के लिए AI model integration जोड़ना होगा।"
            }
        }
    }

    private fun saveHistory() {

        try {
            val array = JSONArray()

            for (i in 0 until messages.childCount) {

                val row = messages.getChildAt(i) as LinearLayout

                if (row.childCount == 0) continue

                val bubble = row.getChildAt(0) as TextView

                val role = if (row.gravity == Gravity.END) {
                    "user"
                } else {
                    "assistant"
                }

                val item = JSONObject()
                item.put("role", role)
                item.put("text", bubble.text.toString())

                array.put(item)
            }

            getSharedPreferences(
                prefsName,
                MODE_PRIVATE
            ).edit().putString(
                historyKey,
                array.toString()
            ).apply()

        } catch (_: Exception) {
            // Ignore history saving errors.
        }
    }

    private fun loadHistory() {

        try {
            val saved = getSharedPreferences(
                prefsName,
                MODE_PRIVATE
            ).getString(historyKey, "[]") ?: "[]"

            val array = JSONArray(saved)

            for (i in 0 until array.length()) {

                val item = array.getJSONObject(i)

                addMessage(
                    item.optString("role", "assistant"),
                    item.optString("text", "")
                )
            }

        } catch (_: Exception) {
            // Start with an empty chat if history is invalid.
        }
    }
}
