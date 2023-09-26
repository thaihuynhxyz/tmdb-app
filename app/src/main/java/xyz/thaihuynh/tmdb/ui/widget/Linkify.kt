package xyz.thaihuynh.tmdb.ui.widget

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun Linkify(url: String, fullStr: String = url, linkify: String = url) {

    // Creating an annonated string
    val mAnnotatedLinkString = buildAnnotatedString {

        // creating a string to display in the Text
        // word and span to be hyperlinked
        val mStartIndex = fullStr.indexOf(linkify)
        val mEndIndex = mStartIndex + linkify.length

        append(fullStr)
        addStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            ), start = mStartIndex, end = mEndIndex
        )

        // attach a string annotation that
        // stores a URL to the text "link"
        addStringAnnotation(
            tag = "URL",
            annotation = url,
            start = mStartIndex,
            end = mEndIndex
        )
    }

    // UriHandler parse and opens URI inside
    // AnnotatedString Item in Browse
    val mUriHandler = LocalUriHandler.current

    ClickableText(
        text = mAnnotatedLinkString,
        onClick = {
            mAnnotatedLinkString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    mUriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}