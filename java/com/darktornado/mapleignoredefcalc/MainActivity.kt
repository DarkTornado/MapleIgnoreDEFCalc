package com.darktornado.mapleignoredefcalc

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import kotlin.math.roundToInt

class MainActivity : Activity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                0 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DarkTornado/MapleIgnoreDEFCalc")))
                1 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.naver.com/dt3141592")))
            }
        } catch (e: Exception) {
            toast(e.toString())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, "깃허브로 이동")
        menu.add(0, 1, 0, "개발자 블로그")
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setDisplayShowHomeEnabled(false)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))

        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt1 = TextView(this)
        val txt2 = EditText(this)
        val txt3 = TextView(this)
        val txt4 = EditText(this)
        val txt5 = TextView(this)
        val txt6 = EditText(this)

        txt1.text = "스탯창 방무 : "
        txt1.textSize = 18f
        layout.addView(txt1)
        txt2.hint = "스탯창에 표시된 방어율 무시 입력"
        txt2.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(txt2)
        txt3.text = "\n나머지 방무(들) : "
        txt3.textSize = 18f
        layout.addView(txt3)
        txt4.hint = "나머지 방어율 무시(들) 입력 (엔터로 구분)"
        layout.addView(txt4)

        val calc = Button(this)
        calc.text = "실방무 계산"
        calc.setOnClickListener {
            val input1 = txt2.text.toString();
            val input2 = txt4.text.toString();
            if (input1.isBlank() || input2.isBlank()) {
                toast("입력되지 않은 값이 있어요.")
            } else {
                val result = calcIgnoreDEF(input1.toDouble(), input2).toString()
                txt6.text = Editable.Factory.getInstance().newEditable(result)
                toast("실방무 계산 결과 약 $result%인거에요.")
            }
        }
        layout.addView(calc)

        txt5.text = "\n계산 결과 : "
        txt5.textSize = 18f
        layout.addView(txt5)
        txt6.hint = "계산된 실방무..."
        txt6.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt6)

        val info = Button(this)
        info.text = "앱 정보 & 도움말"
        info.setOnClickListener {
            showDialog(
                "앱 정보 & 도움말", "앱 이름 : 실방무 계산기\n버전 : 1.2\n개발자 : Dark Tornado\n\n" +
                        " 메이플스토리라는 게임에 있는 '방어율 무시' 스탯을 계산해주는 앱이에요. 스탯창에 뜨는 방어율 무시 수치와, 스킬이나 코어 강화에 붙어있는 방어율 무시 수치(들)를 입력하면 실제로 적용되는 방어율 무시 수치를 계산해주는거에요.\n" +
                        " 스탯창에 뜨는 방어율 무시 수치는 올림된 값이고, 이 앱에서 계산한 결과가 100% 일치한다고 보장하지는 않는거에요\n\n" +
                        " 방무 적용 공식 : 현재방무 + (100 - 현재방무) × 추가되는 방무"
            )
        }
        layout.addView(info)

        val maker = TextView(this)
        maker.text = "\n© 2021-2025 Dark Tornado, All rights reserved.\n"
        maker.textSize = 13f
        maker.gravity = Gravity.CENTER
        layout.addView(maker)

        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)

        if (Build.VERSION.SDK_INT >= 35) scroll.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
            val mlp = view.layoutParams as MarginLayoutParams
            mlp.topMargin = insets.top
            mlp.bottomMargin = insets.bottom
            mlp.leftMargin = insets.left
            mlp.rightMargin = insets.right
            WindowInsets.CONSUMED
        }

        setContentView(scroll)
    }

    private fun calcIgnoreDEF(_current: Double, input: String): Double {
        var current: Double = _current
        val defs = input.split("\n")
        var error = false
        for (def in defs) {
            try{
                val diff = (100.0 - current) * (def.toDouble() / 100)
                current += diff
            } catch (e: java.lang.Exception) {
                error = true
            }
        }

        if (error) toast("숫자가 아닌 것이 입력되어, 해당 값은 제외하고 계산되었습니다.")
        current *= 100
        return current.roundToInt().toDouble() / 100;
    }

    private fun showDialog(title: String, msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setNegativeButton("닫기", null)
        dialog.show()
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    fun dip2px(dips: Int) = Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()

}
