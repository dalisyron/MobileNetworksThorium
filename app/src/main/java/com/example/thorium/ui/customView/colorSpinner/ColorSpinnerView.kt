package com.example.thorium.ui.customView.colorSpinner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thorium.R

class ColorSpinnerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr),
    ColorSpinnerBaseAdapter.OnItemSelectedListener {

    interface OnItemChangeListener {
        fun onChange(colorSpinnerItem: ColorSpinnerItem)
    }

    private var onItemChangeListener: OnItemChangeListener? = null
    private var popupWindow: PopupWindow
    private var listView: RecyclerView
    private var rightDrawable: Drawable? =
        ResourcesCompat.getDrawable(resources, R.drawable.background_spinner_right, null)
    private var dropDownListBackgroundDrawable: Drawable? =
        ResourcesCompat.getDrawable(resources, R.drawable.background_spinner_drop_down, null)
    private var collapseDrawable: Int = R.drawable.background_white
    private var dropDownItemHeight: Int = resources.getDimension(R.dimen.drop_down_height).toInt()
    private var dropDownListWidth: Int = resources.getDimension(R.dimen.drop_down_width).toInt()
    private var dropDownVerticalPadding: Int =
        resources.getDimension(R.dimen.drop_down_vertical_padding).toInt()
    private var popupElevation: Float = resources.getDimension(R.dimen.popupElevation)
    private val onDismissListener = PopupWindow.OnDismissListener {
        setBackgroundResource(collapseDrawable)
    }

    init {
        isClickable = true

        // create recycler view (list view)
        listView = createListView(context)

        // create popup window
        popupWindow = createPopupWindow(context)

        setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }

    fun setAdapter(adapter: ColorSpinnerBaseAdapter) {
        adapter.setListener(this)
        listView.adapter = adapter
    }

    fun selectItem(colorSpinnerItem: ColorSpinnerItem) {
        setupItem(colorSpinnerItem)
        recalculatePopupWindowHeight()
    }

    fun setOnItemChangeListener(onItemChangeListener: OnItemChangeListener){
        this.onItemChangeListener = onItemChangeListener
    }

    private fun setupItem(colorSpinnerItem: ColorSpinnerItem) {
        text = colorSpinnerItem.name
        val drawable = compoundDrawables[2]
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable),
            ContextCompat.getColor(context, colorSpinnerItem.color)
        )
    }

    private fun recalculatePopupWindowHeight() {
        popupWindow.height = calculatePopupWindowHeight()
    }

    private fun expand() {
        if (listView.adapter?.itemCount!! <= 0) {
            return
        }

        popupWindow.showAsDropDown(this)
    }

    private fun collapse() {
        setBackgroundResource(collapseDrawable)

        popupWindow.dismiss()
        invalidate()
    }

    private fun createListView(context: Context) = RecyclerView(context).apply {
        this.overScrollMode = OVER_SCROLL_NEVER
        this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun createPopupWindow(context: Context?) = PopupWindow(context).apply {
        this.contentView = listView
        this.contentView.setPadding(0, dropDownVerticalPadding, 0, dropDownVerticalPadding)
        this.isOutsideTouchable = true
        this.isFocusable = true
        this.elevation = popupElevation
        this.setOnDismissListener(onDismissListener)
        dropDownListBackgroundDrawable?.let {
            this.setBackgroundDrawable(it)
        }
    }

    private fun calculatePopupWindowHeight(): Int {
        return listView.adapter?.itemCount!! * dropDownItemHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        popupWindow.width = dropDownListWidth
        popupWindow.height = calculatePopupWindowHeight()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (isEnabled && isClickable) {
                if (!popupWindow.isShowing) {
                    expand()
                } else {
                    collapse()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onItemSelected(colorSpinnerItem: ColorSpinnerItem, position: Int) {
        setupItem(colorSpinnerItem)
        collapse()

        onItemChangeListener?.onChange(colorSpinnerItem)
    }
}
