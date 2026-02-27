package com.example.smartalarm.feature.alarm.utility

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.R
import com.example.smartalarm.core.presentation.helper.SwipeToDeleteCallback
import com.example.smartalarm.core.utility.extension.toLocalizedString
import com.google.android.material.snackbar.Snackbar
import com.shawnlin.numberpicker.NumberPicker
import java.time.LocalDate
import java.time.LocalTime


// ---------------------------------------------------------------------
// View Extensions
// ---------------------------------------------------------------------

/**
 * Sets this [View]'s background resource only if it differs from the current one.
 *
 * This avoids unnecessary calls to [View.setBackgroundResource], which can trigger
 * redundant redraws and hurt performance.
 *
 * The current background resource ID is tracked using a view tag
 * (`R.id.view_background_tag`), so make sure this ID is defined in your resources.
 *
 * @param backgroundRes Drawable resource ID to set as the background.
 */
fun View.setBackgroundDrawableIfDifferent(@DrawableRes backgroundRes: Int) {
    val currentBackgroundRes = getTag(R.id.view_background_tag) as? Int
    if (currentBackgroundRes != backgroundRes) {
        setBackgroundResource(backgroundRes)
        setTag(R.id.view_background_tag, backgroundRes)
    }
}


// ---------------------------------------------------------------------
// TextView Extension
// ---------------------------------------------------------------------

/**
 * Updates the text of this [TextView] only if the new text is different
 * from the current text.
 *
 * This avoids unnecessary calls to [TextView.setText], preventing unwanted
 * redraws or UI flicker when the text remains unchanged.
 *
 * @param newText The new text to set. If `null`, no change is made.
 */
fun TextView.setTextIfDifferent(newText: String?) {
    if (newText != null && text.toString() != newText) {
        text = newText
    }
}


/**
 * Sets the text color of this [TextView] only if it differs from the current color.
 *
 * This prevents redundant calls to [TextView.setTextColor], avoiding unnecessary redraws
 * and potential performance impacts.
 *
 * The current color resource ID is tracked using a view tag (`R.id.view_text_color_tag`),
 * so ensure this ID is defined in your resources.
 *
 * @param colorRes The color resource ID to apply to the text.
 */
fun TextView.setTextColorIfDifferent(@ColorRes colorRes: Int) {
    val currentColorRes = getTag(R.id.view_text_color_tag) as? Int
    if (currentColorRes != colorRes) {
        setTextColor(context.getColor(colorRes))
        setTag(R.id.view_text_color_tag, colorRes)
    }
}




// ---------------------------------------------------------------------
// EditText Extensions
// ---------------------------------------------------------------------

/**
 * Updates the text of this [EditText] only if the new text is different
 * from the current text.
 *
 * This prevents unnecessary updates and cursor jumps that can occur
 * when setting the same text repeatedly (e.g., during data binding or
 * text synchronization). If the text is changed, the cursor is moved
 * to the end of the new text.
 *
 * Example usage:
 * ```
 * editText.setTextIfDifferent(viewModel.username)
 * ```
 *
 * @param newText The new text to set. If `null`, no change is made.
 */
fun EditText.setTextIfDifferent(newText: String?) {
    if (newText != null && text.toString() != newText) {
        setText(newText)
        setSelection(newText.length)
    }
}



// ---------------------------------------------------------------------
// NumberPicker Extension
// ---------------------------------------------------------------------

/**
 * Updates the [NumberPicker]'s value only if it differs from the current value.
 *
 * This prevents redundant updates and avoids triggering unnecessary
 * [NumberPicker.OnValueChangeListener] callbacks or UI refreshes when
 * the value has not actually changed.
 *
 * It is especially useful when synchronizing UI state with a data source
 * (e.g., LiveData or ViewModel) to prevent infinite update loops.
 *
 * Example usage:
 * ```
 * numberPicker.setValueIfDifferent(viewModel.selectedNumber)
 * ```
 *
 * @param newValue The new value to set on the [NumberPicker].
 */
fun NumberPicker.setValueIfDifferent(newValue: Int) {
    if (value != newValue) {
        value = newValue
    }
}



// ---------------------------------------------------------------------
// Compound Button Extension
// ---------------------------------------------------------------------

/**
 * Updates the checked state of this [CompoundButton] only if it differs
 * from the current state.
 *
 * This prevents redundant calls to [CompoundButton.setChecked], which can
 * unnecessarily trigger [CompoundButton.OnCheckedChangeListener] callbacks
 * or cause unwanted UI updates when the state hasn't actually changed.
 *
 * Useful in data-binding or reactive UI scenarios to avoid feedback loops
 * (for example, when syncing a ViewModel's boolean property with a toggle view).
 *
 * This function can be used with any subclass of [CompoundButton], including:
 * - [android.widget.Switch]
 * - [android.widget.CheckBox]
 * - [android.widget.ToggleButton]
 * - [android.widget.RadioButton]
 *
 * Example usage:
 * ```
 * switch.setCheckedIfDifferent(viewModel.isEnabled)
 * checkBox.setCheckedIfDifferent(user.isSubscribed)
 * ```
 *
 * @param checked The desired checked state (`true` for checked, `false` for unchecked).
 */
fun CompoundButton.setCheckedIfDifferent(checked: Boolean) {
    if (isChecked != checked){
        Log.d("TAG","CompoundButton setCheckedIfDifferent Assigned = $checked")
        isChecked = checked
    }
}




// ---------------------------------------------------------------------
// SeekBar Extensions
// ---------------------------------------------------------------------

/**
 * Sets a simplified progress change listener for this [SeekBar].
 *
 * This extension function allows you to handle only the progress change event
 * without needing to implement the full [SeekBar.OnSeekBarChangeListener] interface.
 * The other callback methods, onStartTrackingTouch and onStopTrackingTouch,
 * are provided as empty implementations.
 *
 * Example usage:
 * ```
 * seekBar.onProgressChangedListener { progress, fromUser ->
 *     Log.d("SeekBar", "Progress: $progress (fromUser = $fromUser)")
 * }
 * ```
 *
 * @param onProgressChanged A callback invoked whenever the [SeekBar]'s progress changes.
 *                          - `progress`: The new progress value.
 *                          - `fromUser`: `true` if the change was initiated by the user.
 */
fun SeekBar.onProgressChangedListener(onProgressChanged: (progress: Int, fromUser: Boolean) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged(progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}



/**
 * Sets the progress of the [SeekBar] to the specified value only if the new progress value
 * is different from the current progress.
 *
 * This function prevents unnecessary updates to the progress, which can be useful for improving
 * performance and avoiding redundant UI updates (e.g., if the progress value hasn't changed).
 *
 * @param newProgress The new progress value to set for the [SeekBar]. The value should be between
 *                    the minimum and maximum values of the [SeekBar], as defined by its configuration.
 *
 * @see SeekBar.setProgress
 */
fun SeekBar.setProgressIfDifferent(newProgress: Int) {
    if (progress != newProgress) {
        progress = newProgress
    }
}





// ---------------------------------------------------------------------
// Int Extensions
// ---------------------------------------------------------------------

/**
 * Converts this integer to a two-digit string representation, localized based on the default locale.
 *
 * This function formats the integer using the default locale and ensures that the resulting string
 * always has at least two digits by padding with a leading zero if necessary. Additionally, it takes
 * the locale into account, meaning it will use localized digits if the locale requires it (for example,
 * Hindi, Marathi, or Nepali will use Devanagari digits).
 *
 * Example usage:
 * ```
 * 5.toLocalizedTwoDigitString()   // returns "05" in English locale, "०५" in Hindi locale
 * 12.toLocalizedTwoDigitString()  // returns "12" in both English and Hindi locales
 * ```
 *
 * @receiver The integer to be formatted.
 * @return A string representing this integer as a two-digit number, using the appropriate locale's numbering system.
 */
fun Int.toLocalizedTwoDigitString(): String {
    return this.toLong().toLocalizedString(true)
}


fun getLocalizedDay(context: Context): String {
    // Get the current day of the week (1 = Monday, 7 = Sunday)
    val currentDay = LocalDate.now().dayOfWeek.value // Monday = 1, Sunday = 7

    // Adjust for Calendar's convention (0 = Sunday, 6 = Saturday)
    val adjustedDay = if (currentDay == 7) 0 else currentDay

    // Fetch the localized day name from the appropriate string array (full weekday names)
    val localizedDay = context.resources.getStringArray(R.array.full_weekdays)[adjustedDay]

    return localizedDay
}


// ---------------------------------------------------------------------
// Local Time Extensions
// ---------------------------------------------------------------------
/**
 * Extension function to format a [LocalTime] object into a localized 12-hour time format with AM/PM.
 *
 * This function takes a [LocalTime] object, extracts the hour and minute, and returns a string
 * representing the time in a 12-hour format with an AM or PM suffix, based on the hour of the day.
 * The format is also localized using the provided [Context] to retrieve the appropriate AM/PM string.
 *
 * @param context The [Context] to access localized resources.
 * @return A string representing the formatted time in a 12-hour format (e.g., "1:30 PM").
 */
fun LocalTime.getFormattedTime(context: Context): String {

    // Extract hour and minute from the LocalTime object
    val hour = this.hour
    val minute = this.minute

    // Get localized AM/PM string based on the hour
    val amPmString = if (hour < 12) {
        context.getString(R.string.am)  // Get "AM" string from resources
    } else {
        context.getString(R.string.pm)  // Get "PM" string from resources
    }

    // Handle 12-hour format conversion:
    // - 0 hours (midnight) is displayed as 12
    // - 13-23 hours are converted to 1-11 (afternoon/evening)
    val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

    // Get the hour in a two-digit format (e.g., "01" for 1 AM)
    val localizedHour = formattedHour.toLocalizedTwoDigitString()

    // Get the minute in a two-digit format (e.g., "03" for 3 minutes)
    val localizedMinute = minute.toLong().toLocalizedString()

    // Return the formatted time string, combining the localized hour, minute, and AM/PM string
    return "$localizedHour:$localizedMinute $amPmString"
}





// ---------------------------------------------------------------------
// Recyclerview Extension
// ---------------------------------------------------------------------

/**
 * Enables swipe-to-delete functionality on a [RecyclerView].
 *
 * This version accepts drawable and color resource IDs, resolving them internally.
 *
 * @param context The context used to resolve resources.
 * @param onItemSwiped Callback triggered when an item is swiped.
 */
fun RecyclerView.enableSwipeToDelete(
    context: Context,
    onItemSwiped: (position: Int) -> Unit
) {
    val deleteIcon = context.resolveDrawable(R.drawable.ic_delete)
    val backgroundColor = context.resolveColor(R.color.swipe_background)
    val cornerRadius = 50f

    val swipeCallback = object : SwipeToDeleteCallback(
        deleteIcon = deleteIcon,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        onItemSwiped = onItemSwiped
    ) {}

    ItemTouchHelper(swipeCallback).attachToRecyclerView(this)
}



// ---------------------------------------------------------------------
// SnackBar Extension
// ---------------------------------------------------------------------

/**
 * Displays a SnackBar with an optional "Undo" action, allowing the user to undo an action.
 *
 * This function shows a SnackBar with the provided message and a customizable "Undo" action.
 * When the user taps the action button, the specified [onUndo] lambda is invoked, which allows
 * the caller to define the undo logic (e.g., reversing a previous action).
 *
 * The default action text is "Undo" (`R.string.undo`), but this can be customized through the
 * [actionResId] parameter. The [duration] parameter controls how long the SnackBar is visible
 * (e.g., `SnackBar.LENGTH_LONG`, `SnackBar.LENGTH_SHORT`, or a custom duration in milliseconds).
 *
 * @param messageResId The resource ID for the message to be displayed in the SnackBar.
 * @param actionResId The resource ID for the action text, which defaults to `R.string.undo`.
 *                    This is the text displayed on the action button.
 * @param duration The duration for which the SnackBar will be displayed. Defaults to SnackBar.LENGTH_LONG.
 * @param onUndo The lambda function to be executed when the "Undo" action is tapped. Typically, this would reverse a previous action.
 */
fun View.showSnackBarWithUndo(
    messageResId: Int,
    actionResId: Int = R.string.undo,
    duration: Int = Snackbar.LENGTH_LONG,
    onUndo: () -> Unit
) {
    Snackbar.make(this, messageResId, duration)
        .setAction(actionResId) { onUndo() }
        .show()
}



// ---------------------------------------------------------------------
// Intent Extension
// ---------------------------------------------------------------------

/**
 * Retrieves a Parcelable extra from the [Intent], with compatibility handling for different Android versions.
 *
 * - For Android versions **Android 13 (API level 33) and above**, it uses the modern `getParcelableExtra(key, T::class.java)` method,
 *   which ensures that the correct type of Parcelable is returned.
 * - For Android versions **below Android 13**, it uses the older `getParcelableExtra(key)` method (with a deprecation suppression)
 *   for backward compatibility.
 *
 * This function simplifies the retrieval of Parcelable extras while handling the platform-specific API changes introduced
 * in Android 13, which requires explicit type information when using `getParcelableExtra`.
 *
 * @param key The key for the Parcelable extra in the Intent.
 * @return The Parcelable object of type [T], or `null` if the extra is not found or cannot be cast to the expected type.
 *
 * @throws ClassCastException If the extra is not of type [T] (on older versions of Android).
 */
inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String): T? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getParcelableExtra(key, T::class.java)
    else @Suppress("DEPRECATION") getParcelableExtra(key)


// ---------------------------------------------------------------------
// Context Extension
// ---------------------------------------------------------------------

/**
 * Retrieves a drawable resource from the [Context] based on the provided [resId].
 * If the drawable is not found (i.e., `null` is returned), it falls back to the drawable
 * identified by [fallbackResId]. If [resId] is `null`, it directly returns the drawable
 * identified by [fallbackResId].
 *
 * This function simplifies retrieving drawable resources with a fallback mechanism,
 * making it easier to handle cases where the primary drawable might be missing.
 *
 * @param resId The resource ID of the drawable to retrieve. This can be `null` if no primary drawable is desired.
 * @param fallbackResId The resource ID of the fallback drawable, used when [resId] is either `null` or invalid.
 * @return The drawable associated with [resId] if it exists, otherwise the fallback drawable.
 */
fun Context.getDrawableOrFallback(@DrawableRes resId: Int?, @DrawableRes fallbackResId: Int): Drawable? {
    return if (resId != null) {
        // Attempt to load the drawable for resId, fall back to fallbackResId if not found
        ContextCompat.getDrawable(this, resId) ?: ContextCompat.getDrawable(this, fallbackResId)
    } else {
        // If resId is null, use the fallback drawable
        ContextCompat.getDrawable(this, fallbackResId)
    }
}

/**
 * Returns a non-null drawable from the given resource ID.
 *
 * @throws IllegalArgumentException if the drawable resource is not found.
 */
fun Context.resolveDrawable(drawableResId: Int): Drawable {
    return ContextCompat.getDrawable(this, drawableResId)
        ?: error("Drawable resource not found: $drawableResId")
}

/**
 * Returns the resolved color from the given color resource ID.
 */
fun Context.resolveColor(colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}

