package com.example.smartalarm.feature.timer.presentation.view.helper


import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Gravity
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.extension.dpToPx
import com.google.android.material.button.MaterialButton

/**
 * A custom [GridLayout] that dynamically creates a numeric keypad with buttons based on
 * the current screen orientation (portrait or landscape).
 *
 * The keypad buttons are styled [MaterialButton]s with configurable labels.
 * This view handles layout adjustments and button click events, exposing
 * an [onKeyPressed] callback to notify about key presses.
 *
 * Usage:
 * 1. Call [setup] with the desired button labels and optional orientation flag.
 * 2. Assign a lambda to [onKeyPressed] to listen for key presses.
 *
 * The keypad adapts its rows and columns based on orientation:
 * - Landscape: 3 rows x 4 columns
 * - Portrait: 4 rows x 3 columns
 *
 * Buttons are sized optimally to fit the available width and height with consistent margins.
 */
class KeypadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    /**
     * Callback invoked when a keypad button is pressed.
     * The pressed button label (e.g., "1", "00", "⌫") is passed as an argument.
     */
    var onKeyPressed: ((String) -> Unit)? = null

    /**
     * Sets up the keypad buttons dynamically based on the given labels and orientation.
     *
     * Responsibilities:
     * 1. Determine rows and columns count depending on [isLandscape].
     * 2. Clear any existing buttons.
     * 3. Post a layout pass to calculate button size based on view dimensions.
     * 4. Create and add buttons with appropriate size, margins, labels, and click listeners.
     *
     * @param labels List of string labels to display on keypad buttons.
     * @param isLandscape Whether the device is in landscape mode; defaults to current orientation.
     */
    fun setup(labels: List<String>, isLandscape: Boolean = isInLandscape()) {
        val rows = if (isLandscape) 3 else 4
        val columns = if (isLandscape) 4 else 3
        val marginPx = 8.dpToPx()

        removeAllViews()
        rowCount = rows
        columnCount = columns

        post {
            val buttonSize = calculateButtonSize(width, height, rows, columns, marginPx)
            labels.forEachIndexed { index, label ->
                val button = createKeypadButton(label, index, columns, buttonSize, marginPx, isLandscape)
                addView(button)
            }
        }
    }

    /**
     * Creates a styled [MaterialButton] for the keypad.
     *
     * Configures appearance, size, margins, and click behavior for the button.
     *
     * @param label Text label to display on the button.
     * @param index Position index of the button in the grid.
     * @param columns Number of columns in the keypad grid.
     * @param size Size in pixels (width and height) for the square button.
     * @param margin Margin in pixels to apply around the button.
     * @param isLandscape Whether the device is in landscape orientation.
     * @return A configured [MaterialButton] ready to be added to the layout.
     */
    private fun createKeypadButton(
        label: String,
        index: Int,
        columns: Int,
        size: Int,
        margin: Int,
        isLandscape: Boolean
    ): MaterialButton {
        val row = index / columns
        val col = index % columns

        return MaterialButton(context).apply {
            text = label
            textSize = if (isLandscape) 12f else 20f
            gravity = Gravity.CENTER
            isAllCaps = false
            elevation = 4f

            background = ContextCompat.getDrawable(context, R.drawable.circular_button)
            //setTextColor(ContextCompat.getColor(context, R.color.white))

            layoutParams = LayoutParams(
                spec(row, 1f),
                spec(col, 1f)
            ).apply {
                width = size
                height = size
                setMargins(margin, margin, margin, margin)
            }

            setOnClickListener {
                onKeyPressed?.invoke(label)
            }
        }
    }

    /**
     * Calculates the optimal size for square keypad buttons within the available grid layout.
     *
     * The size is computed based on total width, height, number of rows and columns,
     * and the margin applied on each side of a button.
     *
     * Calculation:
     * - Determine button width by dividing total width by columns.
     * - Determine button height by dividing total height by rows.
     * - Use the smaller of these two values to ensure buttons fit.
     * - Subtract twice the margin to account for spacing.
     *
     * @param width Total width available for the grid layout.
     * @param height Total height available for the grid layout.
     * @param rows Number of rows in the keypad grid.
     * @param columns Number of columns in the keypad grid.
     * @param margin Margin in pixels on each side of a button.
     * @return Calculated button size in pixels.
     */
    private fun calculateButtonSize(
        width: Int,
        height: Int,
        rows: Int,
        columns: Int,
        margin: Int
    ): Int {
        return minOf(width / columns, height / rows) - (margin * 2)
    }

    /**
     * Checks whether the current device orientation is landscape.
     *
     * @return True if the device orientation is landscape; false otherwise.
     */
    private fun isInLandscape(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

}
