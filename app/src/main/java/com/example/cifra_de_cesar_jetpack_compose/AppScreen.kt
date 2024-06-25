package com.example.cifra_de_cesar_jetpack_compose

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Configuration
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cifra_de_cesar_jetpack_compose.ui.theme.CifradeCesarJetPackComposeTheme
import com.example.cifra_de_cesar_jetpack_compose.ui.theme.Shapes
import com.example.cifra_de_cesar_jetpack_compose.ui.theme.customFont

@Composable
fun AppScreen(cipherViewModel: CipherViewModel = viewModel()) {
    val cipherUiState by cipherViewModel.uiState.collectAsState()
    val centralImageSize: Dp
    val spacerSize: Dp
    val context = LocalContext.current
    val invalidKeyError = stringResource(R.string.error_invalid_key)
    val copiedWarning = stringResource(R.string.copy)
    val successfullyEncrypted = stringResource(R.string.successfully)

    val configuration = LocalConfiguration.current

    // Change the backgroundScreen's size
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            centralImageSize = dimensionResource(R.dimen.smallCentralImageSize)
            spacerSize = dimensionResource(R.dimen.smallSpacerImageSize)
        }
        else -> {
            centralImageSize = dimensionResource(R.dimen.centralImageSize)
            spacerSize = dimensionResource(R.dimen.spacerImageSize)
        }
    }

    Box() {
        BackgroundScreen(
            idImageBackground = R.drawable.cipher_background,
            idImage = R.drawable.cipher_central_image,
            imageSize = centralImageSize,
            modifier = Modifier.matchParentSize()
        )
    }
    CaesarCipherLayout(
        showDialog = { cipherViewModel.showDialogBoxMessage() },
        inputedKey = cipherViewModel.cipherKey,
        changingInputedKey = { cipherViewModel.updateCipherKey(it) },
        encryptMessageButton = {
            cipherViewModel.runEncryptMessage(
                message = cipherViewModel.inputedMessage,
                cipherKey = cipherViewModel.cipherKey
            )
        },
        imageSize = centralImageSize,
        spacerSize = spacerSize
    )
    
    if (cipherUiState.dialogBoxMessage) {
        showDialogInputMessage(
            onDismissRequest = { cipherViewModel.closeDialogBoxMessage() },
            onConfirmation = { cipherViewModel.checkInputedMessage() },
            changingInputedMessage = { cipherViewModel.updateInputedMessage(it) },
            inputedMessage = cipherViewModel.inputedMessage,
            dialogBoxHeight = spacerSize,
            copyToClipBoard = {
                copyClipboard(context, message = cipherViewModel.inputedMessage, warning = copiedWarning)
                showToast(showMessage = copiedWarning, context = context)
            }
        )
    }
    if (cipherUiState.isInvalidKey) {
        showToast(invalidKeyError, context)
        cipherViewModel.resetErrorState()
    }
    if (cipherUiState.isSuccessfullyEncrypted) {
        showToast(successfullyEncrypted, context)
        cipherViewModel.resetErrorState()
    }
}


fun copyClipboard(context: Context, message: String, warning: String) {
    val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(android.content.ClipData.newPlainText(warning, message))

}

// App's Layout
@Composable
fun CaesarCipherLayout(
    changingInputedKey: (String) -> Unit,
    inputedKey: String,
    showDialog: () -> Unit,
    encryptMessageButton: () -> Unit,
    imageSize: Dp,
    spacerSize: Dp,
    modifier: Modifier = Modifier
) {
    val buttonWidth = dimensionResource(R.dimen.buttonsWidth)
    val buttonHeight = dimensionResource(R.dimen.buttonsHeight)
    val context = LocalContext.current
    val encryptedSuccess = stringResource(R.string.successfully)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(spacerSize))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showDialog() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.edit)
                ),
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)

            ) {
                Text(
                    text = stringResource(R.string.edit_message),
                    fontFamily = customFont,
                    fontSize = 22.sp,
                    )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextField(
                value = inputedKey,
                onValueChange = changingInputedKey,
                shape = Shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedContainerColor = colorResource(id = R.color.edit),
                    unfocusedContainerColor = colorResource(id = R.color.edit),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontFamily = customFont,
                    fontSize = 22.sp,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        ImageButton(
            onClick = {
                encryptMessageButton()
            }
        )
    }
}


@Composable
fun showDialogInputMessage(
    changingInputedMessage: (String) -> Unit,
    inputedMessage: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    copyToClipBoard: () -> Unit,
    dialogBoxHeight: Dp,
    modifier: Modifier = Modifier
) {

    val imageBitmap = ImageBitmap.imageResource(R.drawable.copytwo)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogBoxHeight),
                colors = CardDefaults.cardColors(colorResource(id = R.color.edit))
            ) {
                Box(modifier = Modifier) {
                    Image(
                        painter = painterResource(id = R.drawable.cipher_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        TextField(
                            value = inputedMessage,
                            onValueChange = changingInputedMessage,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.lowerCaseLetters),
                                    color = Color.Gray,
                                    fontFamily = customFont,
                                    fontSize = 22.sp)
                                          },
                            textStyle = TextStyle(
                                fontFamily = customFont,
                                fontSize = 22.sp
                            ),
                            shape = Shapes.medium,
                            colors = TextFieldDefaults.colors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                focusedContainerColor = colorResource(id = R.color.edit),
                                unfocusedContainerColor = colorResource(id = R.color.edit),
                                focusedIndicatorColor = colorResource(id = R.color.edit),
                                unfocusedIndicatorColor = colorResource(id = R.color.edit),
                                cursorColor = Color.White,
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { copyToClipBoard() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colorResource(id = R.color.edit)
                    ),
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.smallButtonWidth))
                        .padding(16.dp)
                ) {
                    Icon(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onConfirmation() },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.edit)
                    )
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundScreen(
    idImageBackground: Int,
    idImage: Int,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    val centralImageSize = imageSize

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = idImageBackground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Image(
            painter = painterResource(id = idImage),
            contentDescription = null,
            modifier = Modifier
                .width(centralImageSize)
                .height(centralImageSize)
                .align(alignment = Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

// Encrypt Message Button
@Composable
fun ImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(Color.Transparent),
    ) {
        Image(
            painter = painterResource(R.drawable.encrypt),
            contentDescription = null,
            modifier = Modifier.padding(dimensionResource(R.dimen.paddingBottomScreen)))
    }
}

// Show the toast when error occurs
fun showToast(showMessage: String, context: Context) {
    Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppScreenPreview() {
    CifradeCesarJetPackComposeTheme {

        showDialogInputMessage(
            inputedMessage = "",
            dialogBoxHeight = 200.dp,
            onConfirmation = {},
            copyToClipBoard = {},
            onDismissRequest = {},
            changingInputedMessage = {}
        )
        AppScreen()
    }
}