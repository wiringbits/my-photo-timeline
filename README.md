# My Photo Timeline
My Photo Timeline is a simple command-line application to organize your photos by dates on your local file system.

The app works in Linux/Windows/MacOs, just go to the [releases](https://github.com/wiringbits/my-photo-timeline/releases) and download the latest binary for your system.

## Why?
While not storing your photos in the cloud, you usually end up with lots of directories with unorganized photos, and lots of duplicated ones. My Photo Timeline can take a all such directories, and organize the photos by its creation date (year/month) excluding duplicates.

I initially created this to be able to fill a family album, where I needed to choose some pictures per month, unfortunately, I wasn't able to find any software doing this while holding my photos out of the cloud, and ended up writing it.

## Example
You can collect all your photo directories in a single root directory, and just run the app (`--dry-run` doesn't alter your file system, it's recommended to try that first):
- `./my-photo-timeline --source ~/Desktop/test-photos --output ~/Desktop/test-output --dry-run`

The `test-photos` directory could look like:

```
test-photos
├── img1.jpg
├── img1-again.jpg
├── invalid
│   ├── img2-no-metadata.jpg
├── img3.jpg
├── img4.jpg
├── img5.jpg
```

Producing the `test-output` directory like:

```
test-output
├── duplicated
│   ├── img1-again.jpg
├── invalid
│   ├── img2-no-metadata.jpg
└── organized
    ├── 2009
    │   └── 03-march
    │       ├── img1.jpg
    ├── 2010
    │   ├── 07-july
    │   │   ├── img3.jpg
    │   │   └── img4.jpg
    │   ├── 09-september
    │   │   ├── img5.jpg
```

Where:
- `test-output/duplicated` has the photos were detected as duplicated.
- `test-output/invalid` has the photos (or non-photos) where the app couldn't detect the creation date.
- `test-output/organized` has the photos organized by date, the format being `year/month/photo-name`.

## How?
The source directory is analyzed recursively to find all files including the proper creation date metadata, the duplicate detection uses a SHA-256 to speed up the comparison process.

currently, we look into several potential metadata tags, which could even organize some video files, but such feature is not supported properly yet.

