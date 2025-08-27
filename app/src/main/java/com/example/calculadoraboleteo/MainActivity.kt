package com.example.boleteocalc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val montoInput = findViewById<EditText>(R.id.montoInput)
        val calcularButton = findViewById<Button>(R.id.calcularButton)
        val resultadosLayout = findViewById<LinearLayout>(R.id.resultadosLayout)

        val boletaLiquido = findViewById<TextView>(R.id.boletaLiquido)
        val recibirLiquido = findViewById<TextView>(R.id.recibirLiquido)
        val retencionLiquido = findViewById<TextView>(R.id.retencionLiquido)

        val boletaBruto = findViewById<TextView>(R.id.boletaBruto)
        val recibirBruto = findViewById<TextView>(R.id.recibirBruto)
        val retencionBruto = findViewById<TextView>(R.id.retencionBruto)

        // Formato chileno con puntos de miles
        val symbols = DecimalFormatSymbols(Locale("es", "CL"))
        symbols.groupingSeparator = '.'
        symbols.decimalSeparator = ','
        val formatoPesos = DecimalFormat("$###,###", symbols)

        // Formatear el EditText mientras escribe
        montoInput.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    montoInput.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[\\$.,]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = formatoPesos.format(parsed)
                        current = formatted
                        montoInput.setText(formatted)
                        montoInput.setSelection(formatted.length)
                    } else {
                        current = ""
                        montoInput.setText("")
                    }

                    montoInput.addTextChangedListener(this)
                }
            }
        })

        calcularButton.setOnClickListener {
            val cleanString = montoInput.text.toString().replace("[\\$.,]".toRegex(), "")
            val monto = cleanString.toDoubleOrNull()

            if (monto != null) {
                val porcentajeSII = 0.145

                // ===== Valor Líquido =====    
                val montoBoletaLiquido = monto / (1 - porcentajeSII)
                val retencionLiquidoSII = montoBoletaLiquido - monto
                val montoRecibirLiquido = monto

                // ===== Valor Bruto =====
                val retencionBrutoSII = monto * porcentajeSII
                val montoRecibirBruto = monto - retencionBrutoSII
                val montoBoletaBruto = monto

                resultadosLayout.visibility = LinearLayout.VISIBLE

                // Mostrar Valor Líquido
                boletaLiquido.text = "Monto de la Boleta: ${formatoPesos.format(montoBoletaLiquido)}"
                recibirLiquido.text = "Monto a Recibir: ${formatoPesos.format(montoRecibirLiquido)}"
                retencionLiquido.text = "Retención SII: ${formatoPesos.format(retencionLiquidoSII)}"

                // Mostrar Valor Bruto
                boletaBruto.text = "Monto de la Boleta: ${formatoPesos.format(montoBoletaBruto)}"
                recibirBruto.text = "Monto a Recibir: ${formatoPesos.format(montoRecibirBruto)}"
                retencionBruto.text = "Retención SII: ${formatoPesos.format(retencionBrutoSII)}"
            }
        }
    }
}
