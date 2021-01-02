package com.kostaslou.gifsoundit.settings.view.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.loukwn.feat_settings.R
import com.loukwn.feat_settings.databinding.ViewSelectableOptionViewBinding

class SelectableOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSelectableOptionViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    init {
        initAttributes(context, attrs)
        binding.container.clipToOutline = true
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val attr = context.obtainStyledAttributes(
            attrs,
            R.styleable.SelectableOptionView,
            0,
            0
        )
        try {
            binding.text.text = attr.getString(R.styleable.SelectableOptionView_sov_text)
            binding.icon.setImageDrawable(attr.getDrawable(R.styleable.SelectableOptionView_sov_icon))
        } finally {
            attr.recycle()
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)

        binding.container.isSelected = selected

        if (selected) {
            binding.text.setTextColor(Color.WHITE)
            ImageViewCompat.setImageTintList(binding.icon, null)
        } else {
            val additionalColor = ContextCompat.getColor(context, R.color.text_additional)
            binding.text.setTextColor(additionalColor)
            ImageViewCompat.setImageTintList(binding.icon, ColorStateList.valueOf(additionalColor))
        }
    }
}
