
package com.missai.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.view.View
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var scroll: ScrollView

    private val history = mutableListOf<Pair<String, Boolean>>()

    private val prefs by lazy {
        getSharedPreferences("miss_ai_memory", MODE_PRIVATE)
    }

    private val purple = Color.rgb(167, 139, 250)
    private val background = Color.rgb(13, 11, 20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(background)
            setPadding(dp(16), dp(12), dp(16), dp(8))
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(16))
        }

        val title = TextView(this).apply {
            text = "MISS AI ✦"
            textSize = 27f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Your personal AI companion"
            textSize = 13f
            setTextColor(purple)
        }

        val status = TextView(this).apply {
            text = "● FOUNDATION PREVIEW  ·  OFFLINE"
            textSize = 10f
            setTextColor(Color.rgb(134, 239, 172))
            setPadding(0, dp(8), 0, 0)
        }

        header.addView(title)
        header.addView(subtitle)
        header.addView(status)
        root.addView(header)

        // Chat area
        scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(8), 0, dp(12))
        }

        scroll.addView(messages)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // Input area
        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(10), 0, dp(8))
        }

        input = EditText(this).apply {
            hint = "Talk to Miss..."
            textSize = 15f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.GRAY)
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_SEND

            background = rounded(
                Color.rgb(35, 30, 47),
                24f
            )

            setPadding(
                dp(16), dp(4), dp(12), dp(4)
            )
        }

        val send = TextView(this).apply {
            text = "➤"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(purple, 24f)
            setPadding(dp(16), dp(10), dp(16), dp(10))
        }

        bottom.addView(
            input,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            )
        )

        val sendParams = LinearLayout.LayoutParams(
            dp(56),
            dp(52)
        )
        sendParams.marginStart = dp(8)

        bottom.addView(send, sendParams)
        root.addView(bottom)

        setContentView(root)

        send.setOnClickListener {
            sendMessage()
        }

        input.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        loadHistory()

        if (history.isEmpty()) {
            addMessage(
                "Hey! I'm Miss ✨\n\n" +
                "I'm still being built, but I'm here. " +
                "Tell me something about your day!",
                false
            )
        }
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()

        if (text.isEmpty()) return

        input.text.clear()
        addMessage(text, true)

        val reply = getStarterReply(text)
        addMessage(reply, false)
    }

    // Temporary starter brain.
    // Real local AI model comes in the next stage.

    private fun getStarterReply(message: String): String {

        val text = message.lowercase()

        return when {

            text.contains("hello") ||
            text == "hi" ||
            text.contains("hey miss") ->
                "Hey! 😄 What's happening? Tell me everything."

            text.contains("who are you") ->
                "I'm MISS AI, your personal companion. " +
                "My proper AI brain is coming soon! ✨"

            text.contains("how are you") ->
                "I'm doing great! Just waiting for " +
                "some interesting conversation. 😄"

            text.contains("bored") ->
                "Bored already? 😂 Come on, let's talk. " +
                "What's on your mind?"

            text.contains("call") ||
            text.contains("message") ||
            text.contains("open") ->
                "I understand that phone command, but " +
                "device control isn't connected yet. " +
                "We'll add it with proper permissions."

            text.contains("remember") ->
                "I can save our conversation locally. " +
                "Long-term intelligent memory is a feature " +
                "we'll build separately."

            text.contains("thank") ->
                "You're welcome! 😊"

            else ->
                "I hear you. ❤️ I'm still waiting for " +
                "my local language model, so I can't " +
                "properly understand every topic yet. " +
                "But your message has been saved."
        }
    }

    private fun addMessage(
        text: String,
        isUser: Boolean,
        save: Boolean = true
    ) {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (isUser) Gravity.END else Gravity.START
            setPadding(0, dp(5), 0, dp(5))
        }

        val bubble = TextView(this).apply {
            this.text = text
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(
                dp(14), dp(12), dp(14), dp(12)
            )

            background = rounded(
                if (isUser) {
                    Color.rgb(91, 65, 145)
                } else {
                    Color.rgb(38, 32, 51)
                },
                18f
            )
        }

        row.addView(
            bubble,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        messages.addView(row)

        if (save) {
            history.add(text to isUser)
            saveHistory()
        }

        scroll.post {
            scroll.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun saveHistory() {
        val array = JSONArray()

        history.forEach { item ->
            val obj = JSONObject()
            obj.put("text", item.first)
            obj.put("user", item.second)
            array.put(obj)
        }

        prefs.edit()
            .putString("chat_history", array.toString())
            .apply()
    }

    private fun loadHistory() {

        val stored = prefs.getString("chat_history", null)
            ?: return

        try {
            val array = JSONArray(stored)

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)

                val text = obj.getString("text")
                val user = obj.getBoolean("user")

                history.add(text to user)
                addMessage(text, user, false)
            }

        } catch (e: Exception) {
            history.clear()
            prefs.edit().remove("chat_history").apply()
        }
    }

    private fun rounded(
        color: Int,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius.toInt()).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
