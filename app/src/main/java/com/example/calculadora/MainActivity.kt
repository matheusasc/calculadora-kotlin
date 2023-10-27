package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import kotlin.math.*

class MainActivity : AppCompatActivity() {
    private lateinit var display: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        display = findViewById(R.id.display)

        val button7: AppCompatButton = findViewById(R.id.button_7)
        val button8: AppCompatButton = findViewById(R.id.button_8)
        val button9: AppCompatButton = findViewById(R.id.button_9)
        val buttonDivide: AppCompatButton = findViewById(R.id.button_divide)

        val button4: AppCompatButton = findViewById(R.id.button_4)
        val button5: AppCompatButton = findViewById(R.id.button_5)
        val button6: AppCompatButton = findViewById(R.id.button_6)
        val buttonMultiply: AppCompatButton = findViewById(R.id.button_multiply)

        val button1: AppCompatButton = findViewById(R.id.button_1)
        val button2: AppCompatButton = findViewById(R.id.button_2)
        val button3: AppCompatButton = findViewById(R.id.button_3)
        val buttonSubtract: AppCompatButton = findViewById(R.id.button_subtract)

        val button0: AppCompatButton = findViewById(R.id.button_0)
        val buttonDot: AppCompatButton = findViewById(R.id.button_dot)
        val buttonClear: AppCompatButton = findViewById(R.id.button_clear)
        val buttonAdd: AppCompatButton = findViewById(R.id.button_add)
        val buttonEquals: AppCompatButton = findViewById(R.id.button_equals)

        val buttonSin: AppCompatButton = findViewById(R.id.button_sin)
        val buttonCos: AppCompatButton = findViewById(R.id.button_cos)
        val buttonTan: AppCompatButton = findViewById(R.id.button_tan)
        val buttonLn: AppCompatButton = findViewById(R.id.button_ln)
        val buttonLog: AppCompatButton = findViewById(R.id.button_log)
        val buttonExclamacao: AppCompatButton = findViewById(R.id.button_exclamacao)
        val buttonPi: AppCompatButton = findViewById(R.id.button_pi)
        val buttonE: AppCompatButton = findViewById(R.id.button_e)
        val buttonParent1: AppCompatButton = findViewById(R.id.button_parent1)
        val buttonParent2: AppCompatButton = findViewById(R.id.button_parent2)

        val display2 = findViewById<EditText>(R.id.display2)

        val buttonDel: AppCompatButton = findViewById(R.id.button_del)
        buttonDel.setOnClickListener {
            deleteLastCharacter()
        }

        button7.setOnClickListener { appendToDisplay("7") }
        button8.setOnClickListener { appendToDisplay("8") }
        button9.setOnClickListener { appendToDisplay("9") }
        buttonDivide.setOnClickListener { appendToDisplay("/") }

        button4.setOnClickListener { appendToDisplay("4") }
        button5.setOnClickListener { appendToDisplay("5") }
        button6.setOnClickListener { appendToDisplay("6") }
        buttonMultiply.setOnClickListener { appendToDisplay("*") }

        button1.setOnClickListener { appendToDisplay("1") }
        button2.setOnClickListener { appendToDisplay("2") }
        button3.setOnClickListener { appendToDisplay("3") }
        buttonSubtract.setOnClickListener { appendToDisplay("-") }

        button0.setOnClickListener { appendToDisplay("0") }
        buttonDot.setOnClickListener { appendToDisplay(".") }
        buttonClear.setOnClickListener { clearDisplay() }
        buttonAdd.setOnClickListener { appendToDisplay("+") }
        buttonEquals.setOnClickListener { calculateResult() }

        buttonSin.setOnClickListener { appendToDisplay("sin(") }
        buttonCos.setOnClickListener { appendToDisplay("cos(") }
        buttonTan.setOnClickListener { appendToDisplay("tan(") }
        buttonLn.setOnClickListener { appendToDisplay("ln(") }
        buttonLog.setOnClickListener { appendToDisplay("log(") }
        buttonExclamacao.setOnClickListener { appendToDisplay("!") }
        buttonPi.setOnClickListener { appendToDisplay("π") }
        buttonE.setOnClickListener { appendToDisplay("e") }
        buttonParent1.setOnClickListener { appendToDisplay("(") }
        buttonParent2.setOnClickListener { appendToDisplay(")") }
    }

    private fun deleteLastCharacter() {
        val currentText = display.text.toString()
        if (currentText.isNotEmpty()) {
            val newText = currentText.substring(0, currentText.length - 1)
            display.setText(newText)
        }
    }

    private fun appendToDisplay(value: String) {
        val currentText = display.text.toString()
        display.setText(currentText + value)
    }

    private fun clearDisplay() {
        display.text.clear()
        val display2 = findViewById<EditText>(R.id.display2)
        display2.text.clear()
    }

    private fun calculateResult() {
        val expression = display.text.toString()
        try {
            val result = eval(expression)

            // Atualize o display2 com o resultado
            val display2 = findViewById<EditText>(R.id.display2)
            display2.setText(result.toString())
        } catch (e: Exception) {
            display.setText("Erro")
        }
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expression.length) throw RuntimeException("Caractere inesperado: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm() // adição
                    else if (eat('-'.toInt())) x -= parseTerm() // subtração
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor() // multiplicação
                    else if (eat('/'.toInt())) x /= parseFactor() // divisão
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.toInt())) return parseFactor() // operador unário +, não faz nada
                if (eat('-'.toInt())) return -parseFactor() // operador unário - (negação)
                var x: Double
                val startPos = pos
                if (eat('('.toInt())) { // parênteses
                    x = parseExpression()
                    eat(')'.toInt())
                } else if (ch in '0'.toInt()..'9'.toInt() || ch == '.'.toInt()) {
                    // números
                    while (ch in '0'.toInt()..'9'.toInt() || ch == '.'.toInt()) nextChar()
                    x = expression.substring(startPos, pos).toDouble()
                } else if (ch in 'a'.toInt()..'z'.toInt() || ch == 'π'.toInt() || ch == 'e'.toInt()) {
                    // funções
                    while (ch in 'a'.toInt()..'z'.toInt()) nextChar()
                    val func = expression.substring(startPos, pos)
                    x = parseFactor()
                    if (func == "sqrt") x = sqrt(x)
                    else if (func == "sin") x = sin(x)
                    else if (func == "cos") x = cos(x)
                    else if (func == "tan") x = tan(x)
                    else if (func == "ln") x = ln(x)
                    else if (func == "log") x = log10(x)
                    else if (func == "π") x = PI
                    else if (func == "e") x = E
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^'.toInt())) x = x.pow(parseFactor()) // potência
                return x
            }
        }.parse()
    }
}