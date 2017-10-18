package com.cube.arc.dmsdk.manager

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.cube.arc.dmsdk.BuildConfig
import com.cube.arc.dmsdk.model.BundleInfo
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kamranzafar.jtar.TarInputStream
import java.io.*
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

/**
 * Convenience class for doing DMS requests such as downloading bundle
 */
object ContentManager
{
	var contentUrl: String = ""
	var projectId: Int = 0

	fun getBundleInfo(progress: (percent: Int) -> Unit, callback: (info: BundleInfo?) -> Unit): AsyncTask<Void, Int, Boolean>
	{
		val stringBuff = StringBuilder()
		return getFromHttp(
			url = contentUrl,
			outputBuilder = {bytes, len, total ->
				stringBuff.append(String(bytes, 0, len, Charset.forName("UTF-8")))
			},
			progress = progress,
			callback = {success ->
				val bundleInfo = Gson().fromJson(stringBuff.toString(), BundleInfo::class.java)
				callback.invoke(bundleInfo)
			}
		)
	}

	fun getBundle(path: File, progress: (percent: Int) -> Unit, callback: (success: Boolean) -> Unit): AsyncTask<Void, Int, Boolean>
	{
		val fileStream = FileOutputStream(path)
		return fileStream.use {
			getFromHttp(
				url = "",
				outputBuilder = {bytes, len, total ->
					fileStream.write(bytes, 0, len)
				},
				progress = progress,
				callback = {success ->
					// extract tar
					if (success)
					{
						Thread({
							extractTar(path, path.parentFile)
							path.delete()

							callback.invoke(true)
						}).start()
					}
					else
					{
						callback.invoke(success)
					}
				}
			)
		}
	}

	@SuppressLint("StaticFieldLeak")
	fun getFromHttp(
		url: String,
		outputBuilder: (bytes: ByteArray, len: Int, total: Long) -> Unit,
		progress: (percent: Int) -> Unit,
		callback: (success: Boolean) -> Unit
	): AsyncTask<Void, Int, Boolean>
	{
		return object : AsyncTask<Void, Int, Boolean>()
		{
			override fun onProgressUpdate(vararg values: Int?)
			{
				progress.invoke(values[0] ?: 0)
			}

			override fun doInBackground(vararg params: Void?): Boolean
			{
				val client = OkHttpClient()

				val request = Request.Builder()
					.addHeader("User-Agent", "Android/" + BuildConfig.APPLICATION_ID + "-" + BuildConfig.VERSION_NAME)
					.addHeader("Cache-Control", "max-age=0")
					.url(url)
					.build()

				try
				{
					val response = client.newCall(request).execute()
					val inputStream = response.body()?.byteStream()

					inputStream?.use { inStream ->
						var bytesCopied: Long = 0
						val totalBytes: Long = response.body()?.contentLength() ?: 0
						var totalPercent = 0
						val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
						var bytes = inStream.read(buffer)

						while (bytes >= 0)
						{
							outputBuilder.invoke(buffer, bytes, totalBytes)
							bytesCopied += bytes

							try
							{
								bytes = inStream.read(buffer)
							}
							catch (e: Exception)
							{
								return false
							}

							val newPercent = ((bytesCopied.toDouble() / totalBytes.toDouble()) * 100.0).toInt()
							if (newPercent > totalPercent)
							{
								totalPercent = newPercent
								publishProgress(totalPercent)
							}
						}

						return bytesCopied == totalBytes && response.isSuccessful
					}
				}
				catch (e: Exception)
				{
					e.printStackTrace()
				}

				return false
			}

			override fun onPostExecute(result: Boolean)
			{
				if (!isCancelled)
				{
					callback.invoke(result)
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
	}

	fun extractTar(extractFrom: File, extractTo: File): Boolean
	{
		try
		{
			val buffer = 8192
			var totalRead: Long = 0

			val stream = BufferedInputStream(GZIPInputStream(FileInputStream(extractFrom), buffer), buffer)
			val tis = TarInputStream(stream)

			while (true)
			{
				val file = tis.nextEntry ?: break
				if (file.name == "./") continue

				val extractedFilePath = extractTo.absolutePath + "/" + file.name
				val extractFile = File(extractedFilePath)

				if (file.isDirectory)
				{
					extractFile.mkdirs()
					continue
				}

				// create folders if they do not exist for file
				if (!File(extractFile.parent).exists())
				{
					File(extractFile.parent).mkdirs()
				}

				val fos = FileOutputStream(extractedFilePath)
				val dest = BufferedOutputStream(fos, buffer)

				var count = 0
				val data = ByteArray(buffer)

				while (true)
				{
					count = tis.read(data)

					if (count == -1) break

					dest.write(data, 0, count)
					totalRead += count.toLong()
				}

				dest.flush()
				dest.close()
			}

			tis.close()
		}
		catch (e: IOException)
		{
			e.printStackTrace()
			return false
		}

		return true
	}
}
