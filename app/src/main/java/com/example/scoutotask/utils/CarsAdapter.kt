package com.example.scoutotask.utils

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scoutotask.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class CarsAdapter(var dataList: List<Car>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<CarsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val carMakeTV: TextView = itemView.findViewById(R.id.tvCarMake)
        private val carModelTV: TextView = itemView.findViewById(R.id.tvCarModel)
        private val addImageBtn: Button = itemView.findViewById(R.id.btnAddCarImage)
        private val deleteCarBtn: Button = itemView.findViewById(R.id.btnDeleteCar)
        private val carImg : ImageView = itemView.findViewById(R.id.imgCar)

        init {
            addImageBtn.setOnClickListener { onButton1Click(adapterPosition) }
            deleteCarBtn.setOnClickListener { onButton2Click(adapterPosition) }
        }

        fun bind(data: Car) {
            carMakeTV.text = data.carMake
            carModelTV.text = data.carModel

            if(data.carImage != null) {
                Picasso.get().load(data.carImage).transform(ResizeTransformation(600, 600)).into(carImg)
            } else {
                carImg.setImageResource(R.drawable.no_image)
            }
        }

        private fun onButton1Click(position: Int) {
            itemClickListener.onImgBtnClick(position)
        }

        private fun onButton2Click(position: Int) {
            itemClickListener.onDelBtnClick(position)
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }
    }

    // Custom Picasso Transformation to resize the image without losing quality
    inner class ResizeTransformation(private val targetWidth: Int, private val targetHeight: Int) :
        Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val sourceWidth = source.width
            val sourceHeight = source.height

            val widthRatio = targetWidth.toFloat() / sourceWidth.toFloat()
            val heightRatio = targetHeight.toFloat() / sourceHeight.toFloat()
            val scaleFactor = if (widthRatio < heightRatio) widthRatio else heightRatio

            val scaledWidth = (sourceWidth * scaleFactor).toInt()
            val scaledHeight = (sourceHeight * scaleFactor).toInt()

            val scaledBitmap = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true)

            if (scaledBitmap != source) {
                source.recycle()
            }

            return scaledBitmap
        }

        override fun key(): String {
            return "resize"
        }
    }

    interface OnItemClickListener {
        fun onImgBtnClick(position: Int)
        fun onDelBtnClick(position: Int)
    }
}