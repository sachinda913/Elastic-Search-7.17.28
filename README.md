# 🧾 Elasticsearch Search 7.17.28(2025-Release version)

This project implements a Spring Boot-based search service using **Elasticsearch Java API Client** to efficiently query Database records. It supports full-text search, partial search, filtering, pagination, and sorting.

---

## 🔍 Features

### ✅ Boolean Query Search (`boolQuerySearch`)
Supports dynamic field-based filtering via `@RequestParam` style inputs:
- Multi-field conditional search using `must`, `should`
- Partial match for names using `match_phrase_prefix`
- Date range filtering (`dobfrom`, `dobto`)
- Pagination (`page`, `size`)
- Source field filtering (returns only relevant fields)

### ✅ Match Query Search
Searches a specific field (e.g., `id`, `nic_number`, or any text field like `full_name`) with support for:
- Exact matching on numeric fields (`id`, `nic_number`)
- Fuzzy text matching on string fields
- Sorted by `date_of_birth` in ascending order

### ✅ Multi Match Query Search
Supports searching across **multiple fields simultaneously**:
- Uses `multi_match` for fuzzy matching text across multiple fields
- Automatically applies `terms` query for numeric fields
- Sorted by `date_of_birth`

---

## 📁 Index Mapping (Expected Fields)

- `id`: `long`
- `full_name`: `text`
- `gender`: `keyword`
- `nic_number`: `long`
- `date_of_birth`: `date`

---

## 🛠 Technologies Used

- Java 17+
- Spring Boot 3.4.4
- Elasticsearch Java API Client (`co.elastic.clients`)
- Elasticsearch 7.17.28
- Maven

---
