package se.warting.pdfprinter

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import se.warting.pdfprinter.ui.theme.PDFPrinterTheme
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFPrinterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        PrintButton()
                    }
                }
            }
        }
    }
}

private fun copyRawResourceToFile(rawResId: Int, resources: Resources, cacheDir: File): File {
    val inputStream: InputStream = resources.openRawResource(rawResId)
    val tempFile: File = File.createTempFile("temp_pdf", ".pdf", cacheDir)
    FileOutputStream(tempFile).use { outputStream ->
        val buffer = ByteArray(1024)
        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
    }
    inputStream.close()
    return tempFile
}

private fun print(context: Context) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

    val tempFile = copyRawResourceToFile(R.raw.sample, context.resources, context.cacheDir)

    // Initialize PrintDocumentAdapter
    val printAdapter = PdfDocumentAdapter(context, tempFile.absolutePath)
    val attributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .build()

    val jobName = "${context.getString(R.string.app_name)} Document"

    printManager.print(jobName, printAdapter, attributes)
}


@Composable
fun PrintButton() {
    val context = LocalContext.current

    // Trigger print when you want, perhaps through a Button in your UI
    Button(onClick = {
        print(context)
    }) {
        Text(text = "Print")
    }
}