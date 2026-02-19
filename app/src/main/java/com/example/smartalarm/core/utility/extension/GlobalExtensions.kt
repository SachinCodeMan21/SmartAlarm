package com.example.smartalarm.core.utility.extension

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.icu.text.NumberFormat
import android.icu.util.ULocale
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// Bundle Extension

/**
 * Retrieves a [Parcelable] from the [Bundle] in a type-safe and backward-compatible way.
 *
 * This function uses the appropriate `getParcelable` method depending on the Android SDK version:
 * - For Android 13 (TIRAMISU / API 33) and above, it uses the type-safe `getParcelable(String, Class<T>)`.
 * - For lower API levels, it falls back to the deprecated `getParcelable(String)` method.
 *
 * @param key The key associated with the Parcelable in the Bundle.
 * @return The Parcelable object of type [T], or `null` if not found or of incorrect type.
 */
inline fun <reified T : Parcelable> Bundle.getParcelableCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }
}


// View Extensions

fun View.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    Toast.makeText(this.context, message, duration).show()
}

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackBar = Snackbar.make(this, message, duration)
    if (actionText != null && action != null) {
        snackBar.setAction(actionText) { action() }
    }
    snackBar.show()
}

// Long
fun Long.toLocalizedString(leadingZero: Boolean = false): String {
    val locale = ULocale.getDefault()

    // If the locale is Hindi/Marathi/Nepali, explicitly request Devanagari digits
    val correctedLocale = when (locale.language) {
        "hi", "mr", "ne" -> ULocale(locale.toLanguageTag() + "@numbers=deva")
        else -> locale
    }

    val formatter = NumberFormat.getIntegerInstance(correctedLocale)

    // Apply leading zero formatting if requested
    if (leadingZero) {
        formatter.minimumIntegerDigits = 2
    }

    return formatter.format(this)
}




// Any Extensions
fun Any.logDebug(message: String) {
    Log.d(this::class.java.simpleName, message)
}


// Context Extension

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.getColorCompat(colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}


/**
 * Converts epoch millis to a formatted string with day abbreviation and 12-hour time.
 *
 * Example output: "Mon, 08:00 PM"
 *
 * @receiver The epoch time in milliseconds.
 * @return A formatted string like "Mon, 08:00 PM".
 */
fun Long.toDayTimeString(): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@toDayTimeString
    }
    // Format day as short text (Mon, Tue, etc.) and time as hh:mm a (08:00 PM)
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // "Mon"
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // "08:00 PM"

    val day = dayFormat.format(calendar.time)
    val time = timeFormat.format(calendar.time)

    return "$day, $time"
}

fun Long.toClockTimeFormat(): String {
    val clockFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return clockFormat.format(Date(this))
}

fun Long.toDayMonthFormat(): String {
    val dayFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
    return dayFormat.format(Date(this))
}

// Int Extensions
fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()



/**
 * Extension function to enable swipe-to-delete functionality on any [RecyclerView].
 *
 * @param context The context to use for drawing and theming.
 * @param onSwipe Callback triggered when an item is swiped; provides adapter position.
 * @param getItem Function to retrieve the item at a given adapter position.
 * @param deleteIcon Drawable resource for the delete icon.
 * @param backgroundColor Resource ID of the background color during swipe.
 */
fun RecyclerView.enableSwipeToDelete(
    context: Context,
    onSwipe: (Int) -> Unit,
    getItem: (Int) -> Any,
    @DrawableRes deleteIcon: Int,
    @ColorRes backgroundColor: Int,
    cornerRadius: Float = 16f
) {
    val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

        override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
            val position = vh.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onSwipe(position)
            }
        }

        override fun onChildDraw(
            c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = vh.itemView
                val paint = Paint().apply {
                    color = ContextCompat.getColor(context, backgroundColor)
                    isAntiAlias = true
                }

                val rect = RectF(
                    itemView.left.toFloat(), itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat()
                )
                c.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

                val icon = ContextCompat.getDrawable(context, deleteIcon) ?: return
                val margin = (itemView.height - icon.intrinsicHeight) / 2
                val top = itemView.top + margin
                val bottom = itemView.bottom - margin

                if (dX > 0) {
                    val left = itemView.left + margin
                    val right = left + icon.intrinsicWidth
                    icon.setBounds(left, top, right, bottom)
                } else {
                    val right = itemView.right - margin
                    val left = right - icon.intrinsicWidth
                    icon.setBounds(left, top, right, bottom)
                }

                icon.draw(c)
            }

            super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
        }
    }

    ItemTouchHelper(callback).attachToRecyclerView(this)
}


/**
 * Extension function to show an undo SnackBar for a deleted time zone.
 *
 * @param message The [message] represents the message that show be visible to the user.
 * @param undoTextRes Resource ID for the undo action text (e.g., R.string.undo).
 * @param onUndo Lambda to execute if the undo action is triggered.
 */
fun View.showUndoTimeZoneSnackBar(
    message: String,
    @StringRes undoTextRes: Int,
    onUndo: () -> Unit
) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAction(undoTextRes) {
            onUndo()
        }
        .show()
}


/**
 * Returns the raw float value from a dimen resource (e.g. 0.1f)
 * defined with format="float".
 */
fun Resources.getDimenRawFloat(@DimenRes resId: Int): Float {
    val outValue = TypedValue()
    this.getValue(resId, outValue, true)
    return outValue.float
}


