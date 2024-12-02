# Document Archive

---

## File System

### Directory

```
- Root 1
  - Folder (Desc 1)
    - Folder (Desc 2, Desc 4)
      - Folder (Desc 3)
        - Document
    - Folder (Desc 5)
      - Document
    - Document
        - (Link) Segment 1
        - (Link) Segment 2
        - (Link) Segment 3
  - Folder (Desc 7)
  - Folder (Desc 6, 8)
- Root 2
  - Folder (Desc 9)
```

### Segments

```
- Segment Database
  - Segment 1
  - Segment 2
  - Segment 3
```

## Structure

### Segment

A **segment** contains a string with limited length.

Each segment is kept in an 128-byte sector in the database.

The string is encrypted by a segment-specific key kept in the corresponding
document.

A segment does not need to be removed from the database when deleted.

### Document

A **document** is the smallest data structure that guarantees a complete natural
segment of text.

A document may or may not have an independent key. When it has one, the stored
segment keys and location data are additionally encrypted by this document key.
Otherwise, they can be directly read with the folder key.

### Folder

A **folder** is a collection of documents.

The description of a folder is stored by a segment (128 bytes). This segment is
maintained by its parent, and has a unique random key in addition to the folder
encryption.

A folder may or may not have an independent key. When it has one, the folder
data is encrypted by this key in addition to the parent folder key.

The folder contains a list of folders and a list of documents.

### Root

A **root** is a special type of folder. All documents and folders are accessed
from the root.

A root must be encrypted by a key.

### Index Segment

An **index** segment is a 128-byte slice in an index file. The last 8 bytes 
are reserved for the address of the next segment