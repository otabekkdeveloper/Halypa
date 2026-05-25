package com.example.arkadagapp.repository

import android.content.Context
import com.example.arkadagapp.R
import com.example.arkadagapp.model.BookForPicker

object BookRepository {

    private val books = listOf(
        BookForPicker(1,  "Älem Içre At Gezer",                                                          R.drawable.banner_1,   "books/1/tm_1.pdf"),
        BookForPicker(2,  "Türkmenistan – Bitaraplygyň Mekany",                                           R.drawable.banner_2,   "books/2/tm_2.pdf"),
        BookForPicker(3,  "Ahalteke Bedewi - Biziň Buýsanjymyz we Şöhratymyz",                           R.drawable.banner_3,   "books/3/tm_3.pdf"),
        BookForPicker(4,  "Çaý Melhem hem Ylham",                                                         R.drawable.banner_4,   "books/4/1.pdf"),
        BookForPicker(5,  "Dowlet gushy",                                                                 R.drawable.banner_5,   "books/5/1.pdf"),
        BookForPicker(6,  "Omrumin manysy",                                                               R.drawable.banner_6,   "books/6/1.pdf"),
        BookForPicker(7,  "Abadançylygyň röwşen gadamlary",                                              R.drawable.placeholder,"books/7/1.pdf"),
        BookForPicker(8,  "Ak Şäherim Aşgabat",                                                          R.drawable.placeholder,"books/8/1.pdf"),
        BookForPicker(9,  "Änew müňýyllyklardan gözbaş alýan medeniýet",                                 R.drawable.placeholder,"books/9/1.pdf"),
        BookForPicker(10, "Arşyň Nepisligi",                                                              R.drawable.placeholder,"books/10/1.pdf"),
        BookForPicker(11, "Bagtyýarlyk Saglykdan Başlanýar",                                              R.drawable.placeholder,"books/11/1.pdf"),
        BookForPicker(12, "Bitarap Türkmenistan",                                                         R.drawable.placeholder,"books/12/1.pdf"),
        BookForPicker(13, "Döwlet adam üçindir",                                                          R.drawable.placeholder,"books/13/1.pdf"),
        BookForPicker(14, "Enä Tagzym - Mukaddeslige Tagzym",                                             R.drawable.placeholder,"books/14/1.pdf"),
        BookForPicker(15, "Ganatly bedewler",                                                             R.drawable.placeholder,"books/15/1.pdf"),
        BookForPicker(16, "Garaşsyz, hemişelik Bitarap Türkmenistanyň Harby doktrinasy",                 R.drawable.placeholder,"books/16/1.pdf"),
        BookForPicker(17, "Garaşsyzlyga guwanmak, Watany, halky söýmek bagtdar",                         R.drawable.placeholder,"books/17/1.pdf"),
        BookForPicker(18, "Hakyda göwheri",                                                               R.drawable.placeholder,"books/18/1.pdf"),
        BookForPicker(19, "Ilе döwlet geler bolsa",                                                       R.drawable.placeholder,"books/19/1.pdf"),
        BookForPicker(20, "Ömrümiň manysynyň dowamaty",                                                   R.drawable.placeholder,"books/20/1.pdf"),
        BookForPicker(21, "Parahatçylyk Sazy, Dostluk, Doganlyk Sazy",                                   R.drawable.placeholder,"books/21/1.pdf"),
        BookForPicker(22, "Türkmenistan - sagdynlygyň we ruhubelentligiň ýurdy 2007",                    R.drawable.placeholder,"books/22/1.pdf"),
        BookForPicker(23, "Türkmenistan Durnukly Ösüşiň Maksatlaryna Ýetmegiň Ýolunda",                  R.drawable.placeholder,"books/23/1.pdf"),
        BookForPicker(24, "Türkmenistanda saglygy goraýşy ösdürmediň ylmy esaslary",                     R.drawable.placeholder,"books/24/1.pdf"),
        BookForPicker(25, "Türkmenistan - sagdynlygyň we ruhubelentligiň ýurdy",                         R.drawable.placeholder,"books/25/1.pdf"),
        BookForPicker(26, "Türkmenistanyň Beýik Galkynyş eýýamynyň Konstitusiýasy hakynda",              R.drawable.placeholder,"books/26/1.pdf"),
        BookForPicker(27, "Türkmenistanyň Bitaraplygy parahatçylygyň we ösüşiň syýasaty",                R.drawable.placeholder,"books/27/1.pdf"),
        BookForPicker(28, "Türkmenistanyň durmuş-ykdysady ösüşiniň döwlet kadalaşdyrylyşy",              R.drawable.placeholder,"books/28/1.pdf"),
        BookForPicker(29, "Türkmenistanyň ykdysady strategiýasy halka daýanyp, halkyň hatyrasyna",       R.drawable.placeholder,"books/29/1.pdf"),
        BookForPicker(30, "Türkmenistan - melhemler mekany",                                              R.drawable.placeholder,"books/30/1.pdf"),
    )

    fun getAllBooks(context: Context): List<BookForPicker> = books

    // Без Context — для SearchIndexManager
    fun getStaticBookById(id: Int): BookForPicker? = books.find { it.id == id }
}