package com.example.proporcje

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    KalkulatorProporcji()
                }
            }
        }
    }
}

@Composable
fun KalkulatorProporcji() {
    val context = LocalContext.current

    var goraLewo by remember { mutableStateOf("4") }
    var dolLewo by remember { mutableStateOf("5") }
    var goraPrawo by remember { mutableStateOf("6") }
    var dolPrawo by remember { mutableStateOf("x") }

    val przyciskColor = Color(0xFF164E63)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Uwaga: Jedną z wartości wejściowych musi być x",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PoleLiczbowe(value = goraLewo, onValueChange = { goraLewo = it })
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(100.dp).height(2.dp).background(Color.LightGray))
                Spacer(modifier = Modifier.height(8.dp))
                PoleLiczbowe(value = dolLewo, onValueChange = { dolLewo = it })
            }

            Text(
                text = "=",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFF333333)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PoleLiczbowe(value = goraPrawo, onValueChange = { goraPrawo = it })
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(100.dp).height(2.dp).background(Color.LightGray))
                Spacer(modifier = Modifier.height(8.dp))
                PoleLiczbowe(value = dolPrawo, onValueChange = { dolPrawo = it })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Button(
                onClick = {
                    val wynik = obliczProporcje(goraLewo, dolLewo, goraPrawo, dolPrawo)
                    if (wynik != null) {
                        if (goraLewo.lowercase() == "x") goraLewo = wynik
                        if (dolLewo.lowercase() == "x") dolLewo = wynik
                        if (goraPrawo.lowercase() == "x") goraPrawo = wynik
                        if (dolPrawo.lowercase() == "x") dolPrawo = wynik
                    } else {
                        Toast.makeText(context, "Błędne dane! Wprowadź dokładnie jeden 'x'.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = przyciskColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Oblicz", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    goraLewo = ""
                    dolLewo = ""
                    goraPrawo = ""
                    dolPrawo = "x"
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = przyciskColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Reset", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PoleLiczbowe(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || input.matches(Regex("^[0-9xX.,]*$"))) {
                onValueChange(input)
            }
        },
        modifier = Modifier.width(110.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, textAlign = TextAlign.Center),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

fun obliczProporcje(gl: String, dl: String, gp: String, dp: String): String? {
    val a = gl.replace(",", ".").lowercase()
    val b = dl.replace(",", ".").lowercase()
    val c = gp.replace(",", ".").lowercase()
    val d = dp.replace(",", ".").lowercase()

    val lista = listOf(a, b, c, d)
    if (lista.count { it == "x" } != 1) return null

    val nA = a.toDoubleOrNull()
    val nB = b.toDoubleOrNull()
    val nC = c.toDoubleOrNull()
    val nD = d.toDoubleOrNull()

    try {
        val wynik = when {
            a == "x" -> (nB! * nC!) / nD!
            b == "x" -> (nA! * nD!) / nC!
            c == "x" -> (nA! * nD!) / nB!
            d == "x" -> (nB! * nC!) / nA!
            else -> return null
        }
        if (wynik.isInfinite() || wynik.isNaN()) return null
        return if (wynik % 1 == 0.0) wynik.toInt().toString() else String.format("%.4f", wynik).replace(",", ".")
    } catch (e: Exception) {
        return null
    }
}
