# ⏰ Görevle Kapanan Alarm Uygulaması - Proje İsterleri

> **Tarih:** 2026-01-11  
> **Platform:** Native Android (Kotlin)  
> **Hedef:** MVP - En kısa sürede temel uygulama

---

## 📱 Genel Bilgiler

| Özellik | Değer |
|---------|-------|
| Platform | Android (Native Kotlin) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 (Android 14) |
| UI Yaklaşımı | Minimalist |
| Tema | Light + Dark Mode |
| Depolama | Local (Room Database) |
| Maksimum Alarm | 3 adet |

---

## ✅ MVP Kapsam (v1.0)

### Dahil Olan Özellikler

#### 1. Alarm Yönetimi
- [x] Alarm oluşturma (saat/dakika)
- [x] Alarm düzenleme
- [x] Alarm silme
- [x] Alarm açma/kapama toggle
- [x] Maksimum 3 alarm limiti
- [x] Tekrarlı alarm (belirli günler seçimi: Pzt, Sal, Çar, Per, Cum, Cmt, Paz)
- [x] Tek seferlik alarm
- [x] Sistem varsayılan alarm sesleri

#### 2. Görev Türleri (3 adet)
- [x] 🚶 **Adım Atma Görevi** - Belirlenen sayıda adım at (sabit: 30 adım)
- [x] 📱 **Telefonu Dikey Tutma Görevi** - Telefonu dikey tut (sabit: 20 saniye)
- [x] ⏳ **Zaman Gecikmeli Kapatma** - Bekle ve kapat (sabit: 15 saniye)

#### 3. Alarm Çalma Ekranı
- [x] Tam ekran alarm arayüzü
- [x] Geri tuşu engelleme
- [x] Sistem navigation engelleme
- [x] Görev talimatları gösterimi
- [x] İlerleme göstergesi (adım sayacı, süre sayacı vb.)
- [x] Pasif kapatma butonu (görev tamamlanınca aktif)

#### 4. Snooze (Erteleme)
- [x] Erteleme butonu
- [x] Sabit erteleme süresi (5 dakika)
- [x] Aynı görev tekrarı

#### 5. UI/UX
- [x] Minimalist tasarım
- [x] Karanlık mod desteği
- [x] Sistem temasına uyum
- [x] Basit ve anlaşılır arayüz

---

## ❌ MVP Dışı (Gelecek Güncellemeler)

| Özellik | Planlanan Versiyon |
|---------|-------------------|
| 🎤 Sesli Komut Görevi | v2.0 |
| 💡 Ortam Işığı Görevi | v2.0 |
| 📊 İstatistik/Geçmiş Ekranı | v2.0 |
| 🔧 Görev Zorluk Seviyeleri | v2.0 |
| 💎 Premium Plan | v2.0 |
| 🎵 Özel Alarm Sesleri | v2.0 |
| ☁️ Bulut Senkronizasyon | v3.0 |
| 🏆 Başarı Rozetleri | v3.0 |

---

## 🏗️ Teknik Mimari

### Kullanılacak Teknolojiler

```
├── Language: Kotlin
├── UI: Jetpack Compose (Modern UI)
├── Architecture: MVVM + Clean Architecture
├── DI: Hilt
├── Database: Room
├── Async: Kotlin Coroutines + Flow
├── Alarm: AlarmManager + Foreground Service
├── Sensors: 
│   ├── Step Counter (TYPE_STEP_COUNTER)
│   └── Accelerometer (TYPE_ACCELEROMETER)
└── Permissions:
    ├── SCHEDULE_EXACT_ALARM
    ├── USE_FULL_SCREEN_INTENT
    ├── FOREGROUND_SERVICE
    ├── ACTIVITY_RECOGNITION (adım sayar için)
    ├── VIBRATE
    └── RECEIVE_BOOT_COMPLETED
```

### Modül Yapısı

```
alarm-app/
├── app/                    # Ana uygulama modülü
├── core/
│   ├── common/            # Ortak utility'ler
│   ├── database/          # Room DB
│   └── ui/                # Ortak UI bileşenleri, tema
├── feature/
│   ├── alarm-list/        # Alarm listesi ekranı
│   ├── alarm-edit/        # Alarm oluştur/düzenle
│   ├── alarm-ring/        # Alarm çalma ekranı
│   └── tasks/             # Görev implementasyonları
└── service/               # Alarm servisi
```

---

## 📱 Ekranlar

### 1. Ana Ekran (Alarm Listesi)
- Mevcut alarmların listesi
- Her alarmda: Saat, günler, görev tipi, açık/kapalı toggle
- FAB ile yeni alarm ekleme
- Boş durum mesajı

### 2. Alarm Oluştur/Düzenle Ekranı
- Saat/Dakika seçici (Time Picker)
- Gün seçimi (Chip group)
- Görev seçimi (3 seçenek)
- Alarm sesi seçimi (dropdown)
- Kaydet/İptal butonları

### 3. Alarm Çalma Ekranı
- Tam ekran (Full Screen Intent)
- Saat gösterimi
- Görev talimatı ve ilerleme
- Kapatma butonu (pasif → aktif)
- Ertele butonu

---

## 🔒 İzinler ve Özel Durumlar

### Gerekli İzinler
1. **SCHEDULE_EXACT_ALARM** - Tam zamanında alarm
2. **USE_FULL_SCREEN_INTENT** - Kilitli ekranda tam ekran
3. **FOREGROUND_SERVICE** - Arka plan servisi
4. **ACTIVITY_RECOGNITION** - Adım sayar için
5. **VIBRATE** - Titreşim
6. **RECEIVE_BOOT_COMPLETED** - Cihaz yeniden başlayınca alarmları kur

### Özel Durumlar
- Battery optimization devre dışı bırakma rehberi
- DND (Rahatsız Etme) modunda çalma
- Telefon kilitli iken alarm ekranı

---

## 🎨 Tasarım Prensipleri

- **Minimalist:** Gereksiz öğe yok
- **Kontrast:** Kolay okunabilirlik
- **Tutarlılık:** Material Design 3
- **Erişilebilirlik:** Büyük dokunmatik alanlar
- **Renk Paleti:** Sakin, göz yormayan tonlar

---

## 📋 Kabul Kriterleri

### Alarm Oluşturma
- [ ] Kullanıcı saat ve dakika seçebilmeli
- [ ] Kullanıcı tekrar günlerini seçebilmeli
- [ ] Kullanıcı 3 görev türünden birini seçebilmeli
- [ ] Maksimum 3 alarm oluşturulabilmeli

### Alarm Çalma
- [ ] Alarm belirlenen saatte çalmalı
- [ ] Tam ekran alarm gösterilmeli
- [ ] Geri tuşu ile çıkılamamalı
- [ ] Görev tamamlanmadan alarm kapanmamalı

### Görevler
- [ ] Adım atma: 30 adım sayılmalı
- [ ] Dikey tutma: 20 saniye boyunca dikey kalmalı
- [ ] Zaman gecikmeli: 15 saniye beklemeli

### Snooze
- [ ] 5 dakika erteleme çalışmalı
- [ ] Erteleme sonrası aynı görev tekrarlanmalı

---

## 🚀 Sonraki Adımlar

1. ✅ Proje isterleri onayı
2. ⬜ Proje yapısı oluşturma
3. ⬜ Veritabanı şeması
4. ⬜ UI tasarımı
5. ⬜ Core modüller
6. ⬜ Feature modüller
7. ⬜ Test ve debug
8. ⬜ Play Store yayını

---

> **Not:** Bu doküman MVP v1.0 kapsamını tanımlar. Onaylandıktan sonra geliştirmeye başlanacaktır.
