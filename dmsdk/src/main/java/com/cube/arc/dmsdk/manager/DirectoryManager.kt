package com.cube.arc.dmsdk.manager

import com.cube.arc.dmsdk.model.Directory
import com.google.gson.JsonElement
import java.io.InputStream

/**
 * Manager class for loading data source into usable list of [Directory] for easy access
 */
object DirectoriesManager
{
	/**
	 * Internal list of [Directory] loaded via [init]. Do not set this via assign, do so via any [init] method
	 */
	var directories: List<Directory> = listOf()
		set(value){}

	/**
	 * Initialises the manager instance with a stream to a file to decode into [directories]
	 */
	public fun init(dataSource: InputStream)
	{

	}

	/**
	 * Initialises the manager instance with a String representation of [directories]
	 */
	public fun init(dataSource: String)
	{

	}

	/**
	 * Initialises the manager instance with a [JsonElement] object
	 */
	public fun init(dataSource: JsonElement)
	{

	}

	/**
	 * Initialises the manager instance with a list of [Directory] models
	 */
	public fun init(dataSource: List<Directory>)
	{

	}

	//////

	/**
	 * Gets a directory for a given [id], or `null` if one was not found
	 */
	public fun directory(id: Int, subList: List<Directory> = directories): Directory?
	{
		return null
	}

	/**
	 * Searches for a directory within the given [root] [Directory]
	 * @return null if one was not found
	 */
	public fun search(root: Directory, id: Int): Directory?
	{
		return null
	}
}
