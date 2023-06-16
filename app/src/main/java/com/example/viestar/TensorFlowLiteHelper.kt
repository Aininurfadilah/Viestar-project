package com.example.viestar

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel


class TensorFlowLiteHelper(private val context: Context, private val modelPath: String) {
    private lateinit var interpreter: Interpreter

    fun initializeInterpreter() {
        val options = Interpreter.Options()
        interpreter = Interpreter(loadModelFile(), options)
    }

    private fun loadModelFile(): ByteBuffer {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelPath).fileDescriptor
        val inputStream = FileInputStream(fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileChannel.position()
        val declaredLength = fileChannel.size() - startOffset
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            .order(ByteOrder.nativeOrder())
    }

    fun runInference(inputData: String): FloatArray {
        val outputData = FloatArray(outputSize)
        interpreter.run(inputData, outputData)
        return outputData
    }

    companion object {
        private const val outputSize = 30000 // define the size of the output
    }
}