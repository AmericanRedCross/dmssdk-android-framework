# Readme

This is the DMSDK for the [DMS]() system created to manage documents in a hierarchical way.

# Usage

Either download this SDK and include it into your project, or you can copy the aar directly from [here (v1.0)]()

The library is made up of 2 parts, the `DirectoryManager` singleton, and the data structures.

The `DirectoryManager` class is the main source used to initialise a structure json file into workable models and hosts a few convenience methods.

Use one of the 4 `init` methods within `DirectoryManager` to load a structure ([example](sdk/dmsdk/src/test/resources/structure.json)). You will then be able to access directories via `DirectoryManager.directories` or the `DirectoryManager.directory(id)` methods

## Pre-requisites

Android Studio 3 (version RC1 or newer), Kotlin lang version `1.1.3-2`

The SDK is built with android API `17` as minSDK and currently only supports up to API `25`

## TODO

Currently in development on `feature/content-manager` is an additional manager class to be used for downloading standard files and bundles from the DMS system.

## License
