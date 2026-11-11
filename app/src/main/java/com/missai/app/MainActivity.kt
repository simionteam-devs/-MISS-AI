package com.missai.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.text.InputType
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var messages: LinearLayout
    private lateinit var input: EditText
    private lateinit var scrollView: ScrollView

    private val prefsName = "miss_ai_preferences"
    private val historyKey = "chat_history"

    private val bg = Color.rgb(12, 8, 23)
    private val panel = Color.rgb(25, 16, 42)
    private val purple = Color.rgb(139, 45, 255)
    private val pink = Color.rgb(225, 65, 255)
    private val cyan = Color.rgb(65, 190, 255)
    private val white = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createScreen()
        loadHistory()

        if (messages.childCount == 0) {
            addMessage(
                "assistant",
                "नमस्ते! 👋 मैं MISS AI हूँ। तुम्हारी personal AI companion. बताओ, आज क्या करना है?"
            )
        }
    }

    private fun gradient(
        colors: IntArray,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            colors
        ).apply {
            cornerRadius = radius
        }
    }

    private fun solid(
        color: Int,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
    }

    private fun makeText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun createScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(panel)
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_miss_ai)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        header.addView(
            logo,
            LinearLayout.LayoutParams(52, 52)
        )

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 0, 0, 0)
        }

        val brandTitle = makeText("MISS AI", 23f, white, true)
        val brandSubtitle = makeText(
            "Your Personal AI Companion",
            11f,
            Color.rgb(190, 160, 225)
        )

        brand.addView(brandTitle)
        brand.addView(brandSubtitle)

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        val status = makeText("●", 14f, Color.rgb(70, 230, 160))
        header.addView(status)

        root.addView(
            header,
            LinearLayout.LayoutParams(-1, -2)
        )

        val line = android.view.View(this).apply {
            setBackgroundColor(Color.rgb(100, 35, 160))
        }

        root.addView(
            line,
            LinearLayout.LayoutParams(-1, 1)
        )

        // Chat area
        scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        messages = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 18, 14, 18)
        }

        scrollView.addView(messages)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams( -1, 0, 1f)
        )

        // Voice visual
        val voiceArea = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 12)
        }

        val voiceButton = TextView(this).apply {
            text = "▂▅▇▅▂"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(white)
            background = gradient(
                intArrayOf(pink, purple, cyan),
                60f
            )
            setPadding(28, 8, 28, 8)
            setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Voice feature will be added soon.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        voiceArea.addView(
            voiceButton,
            LinearLayout.LayoutParams(-2, 52)
        )

        val voiceLabel = makeText(
            "TAP TO TALK • COMING SOON",
            10f,
            Color.rgb(170, 135, 210),
            true
        )

        voiceLabel.setPadding(0, 7, 0, 0)
        voiceArea.addView(voiceLabel)

        root.addView(
            voiceArea,
            LinearLayout.LayoutParams(-1, -2)
        )

        // Message composer
        val composer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 10, 12, 10)
            setBackgroundColor(panel)
        }

        input = EditText(this).apply {
            hint = "Message MISS AI..."
            setHintTextColor(Color.rgb(160, 140, 185))
            setTextColor(white)
            textSize = 15f
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            imeOptions = EditorInfo.IME_ACTION_SEND
            setPadding(16, 0, 16, 0)
            background = solid(
                Color.rgb(43, 29, 65),
                45f
            )
        }

        composer.addView(
            input,
            LinearLayout.LayoutParams(0, 52, 1f).apply {
                marginEnd = 9
            }
        )

        val send = TextView(this).apply {
            text = "➤"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(white)
            background = gradient(
                intArrayOf(pink, purple),
                50f
            )
            setOnClickListener {
                sendMessage()
            }
        }

        composer.addView(
            send,
            LinearLayout.LayoutParams(54, 52)
        )

        root.addView(
            composer,
            LinearLayout.LayoutParams(-1, -2)
        )

        // Bottom navigation
        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 14)
            setBackgroundColor(Color.rgb(18, 11, 31))
        }

        val chatTab = navItem("⌂", "Chat", true)
        val historyTab = navItem("◷", "History", false)
        val settingsTab = navItem("⚙", "Settings", false)

        navigation.addView(
            chatTab,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        navigation.addView(
            historyTab,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        navigation.addView(
            settingsTab,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        historyTab.setOnClickListener {
            Toast.makeText(
                this,
                "Your conversations are saved automatically.",
                Toast.LENGTH_SHORT
            ).show()
        }

        settingsTab.setOnClickListener {
            Toast.makeText(
                this,
                "Settings will be added soon.",
                Toast.LENGTH_SHORT
            ).show()
        }

        root.addView(navigation)

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

    private fun navItem(
        symbol: String,
        label: String,
        selected: Boolean
    ): LinearLayout {

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(8, 3, 8, 3)

            val icon = makeText(
                symbol,
                24f,
                if (selected) pink else Color.LTGRAY
            )

            val title = makeText(
                label,
                11f,
                if (selected) pink else Color.LTGRAY,
                selected
            )

            addView(icon)
            addView(title)
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
        }, 400)
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
            setPadding(2, 7, 2, 7)
            tag = role
        }

        val bubble = TextView(this).apply {
            this.text = text
            textSize = 15f
            setTextColor(white)
            setPadding(16, 13, 16, 13)
            maxWidth = (resources.displayMetrics.widthPixels * 0.78f).toInt()

            background = if (role == "user") {
                gradient(
                    intArrayOf(
                        Color.rgb(120, 35, 230),
                        Color.rgb(190, 45, 235)
                    ),
                    24f
                )
            } else {
                solid(
                    Color.rgb(43, 29, 65),
                    24f
                )
            }
        }

        row.addView(
            bubble,
            LinearLayout.LayoutParams(-2, -2)
        )

        messages.addView(
            row,
            LinearLayout.LayoutParams(-1, -2)
        )

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        saveHistory()
    }

    private fun generateReply(message: String): String {

        val text = message.lowercase(Locale.getDefault())

        return when {

            text.contains("hello") ||
            text.contains("hi") ||
            text.contains("नमस्ते") ||
            text.contains("हेलो") ->
                "नमस्ते! 😊 बताओ, आज क्या बात करनी है?"

            text.contains("your name") ||
            text.contains("तुम्हारा नाम") ->
                "मेरा नाम MISS AI है। मैं तुम्हारी personal assistant हूँ।"

            text.contains("how are you") ||
            text.contains("कैसी हो") ->
                "मैं ठीक हूँ! तुम बताओ, कैसे हो?"

            text.contains("time") ||
            text.contains("समय") ->
                "अभी का समय: ${
                    SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                    ).format(Date())
                }"

            text.contains("date") ||
            text.contains("तारीख") ->
                "आज की तारीख: ${
                    SimpleDateFormat(
                        "dd MMMM yyyy",
                        Locale.getDefault()
                    ).format(Date())
                }"

            text.contains("thank") ||
            text.contains("धन्यवाद") ||
            text.contains("शुक्रिया") ->
                "तुम्हारा स्वागत है! ❤️"

            text.contains("bye") ||
            text.contains("बाय") ->
                "फिर मिलेंगे! अपना ख्याल रखना।"

            else ->
                "मैंने तुम्हारी बात समझने की कोशिश की। अभी मेरी offline intelligence सीमित है। असली AI responses के लिए AI model integration जोड़ना होगा।"
        }
    }

    private fun saveHistory() {

        try {
            val array = JSONArray()

            for (i in 0 until messages.childCount) {

                val row = messages.getChildAt(i) as LinearLayout

                if (row.childCount == 0) continue

                val bubble = row.getChildAt(0) as TextView

                val item = JSONObject()
                item.put("role", row.tag?.toString() ?: "assistant")
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
            // Ignore history errors.
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
            // Start with empty history.
        }
    }
}
