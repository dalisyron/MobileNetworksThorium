package com.example.thorium.ui.detail

import android.content.Context
import androidx.annotation.StyleRes
import com.example.common.entity.CellGsm
import com.example.common.entity.CellLog
import com.example.common.entity.CellLte
import com.example.common.entity.CellWcdma
import com.example.thorium.R
import com.example.thorium.databinding.BottomSheetCellLogDetailBinding
import com.example.thorium.util.getFormattedDate
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.IllegalArgumentException

class CellLogDetailBottomSheetDialog @JvmOverloads constructor(
    context: Context,
    @StyleRes theme: Int = 0
) : BottomSheetDialog(context) {

    private val binding: BottomSheetCellLogDetailBinding =
        BottomSheetCellLogDetailBinding.inflate(layoutInflater)
    private var cellLog: CellLog? = null

    init {
        setContentView(binding.root)
    }

    private fun loadCellLog() {
        cellLog?.let { cellLog ->
            val codePrefix: String?
            val cellTitle: String?
            val locationPrefix: String?

            val cell = cellLog.cell
            when (cell) {
                is CellLte -> {
                    codePrefix = context.getString(R.string.gen4_code_prefix)
                    locationPrefix = context.getString(R.string.prefix_location4)
                    cellTitle = context.getString(R.string.cell_4g)
                }
                is CellWcdma -> {
                    codePrefix = context.getString(R.string.gen3_code_prefix)
                    locationPrefix = context.getString(R.string.prefix_location3)
                    cellTitle = context.getString(R.string.cell_3g)
                }
                is CellGsm -> {
                    codePrefix = context.getString(R.string.gen2_code_prefix)
                    locationPrefix = context.getString(R.string.prefix_location2)
                    cellTitle = context.getString(R.string.cell_2g)
                }
                else -> throw IllegalArgumentException()
            }

            binding.tvCode.text = context.getString(
                R.string.code,
                cell.cellCode.toString()
            ).replace("Code", codePrefix)

            binding.tvLocationCode.text = context.getString(
                R.string.locationcode,
                cell.locationId.toString()
            ).replace("LocationCode", locationPrefix)

            binding.tvTitle.text = cellTitle
            binding.tvLogDate.text =
                context.getString(R.string.date, getFormattedDate(cellLog.dateCreated))
            binding.tvMcc.text = context.getString(R.string.mcc, cell.mcc.toString())
            binding.tvMnc.text = context.getString(R.string.mnc, cell.mnc.toString())
            binding.tvStrength.text =
                context.getString(R.string.strength, cell.strength.toString() + " dBm")
            val registeredText =
                if (cell.registered) context.getString(R.string.yes) else context.getString(
                    R.string.no
                )
            binding.tvRegistered.text = context.getString(R.string.registered, registeredText)
            binding.tvDnsResolveTime.text = context.getString(
                R.string.dns_resolve_time,
                (cellLog.dnsResolveTimeMillis).toString()
            )
            binding.tvUpstreamThroughput.text = context.getString(
                R.string.upstream_throughput,
                (cellLog.upstreamLinkThroughputKbps).toString()
            )
            binding.tvDownstreamThroughput.text = context.getString(
                R.string.downstream_throughput,
                (cellLog.downstreamLinkThroughputKbps).toString()
            )

        }
    }

    companion object {
        fun getInstance(context: Context, cellLog: CellLog): CellLogDetailBottomSheetDialog {
            return CellLogDetailBottomSheetDialog(context).apply {
                this.cellLog = cellLog
                loadCellLog()
            }
        }
    }
}