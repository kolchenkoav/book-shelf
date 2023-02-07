package org.example.web.controllers;

import org.apache.log4j.Logger;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/books")
@Scope("singleton")
public class BookShelfController {

    private final Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        logger.info(this.toString());
        model.addAttribute("book", new Book());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(Book book) {
        //TODO Исключить возможность сохранения пустых записей
        boolean err = false;
        if (book.getAuthor().isEmpty()) {
            logger.info("Author is empty: ");
            err = true;
        }
        if (book.getTitle().isEmpty()) {
            logger.info("Title is empty: ");
            err = true;
        }
        if (book.getSize() == null) {
            logger.info("size (pages) is empty: ");
            err = true;
        }
        if (!err) {
            bookService.saveBook(book);
            logger.info("current repository size: " + bookService.getAllBooks().size());
        }
        return "redirect:/books/shelf";
    }

    @PostMapping("/remove")
    public String removeBook(@RequestParam(value = "bookIdToRemove") String bookIdToRemove) {
        //TODO Устранён баг при попытке удаления записи по несуществующему id
        if (!bookService.removeBookById(bookIdToRemove)) {
            logger.info("not found book ID number: " + bookIdToRemove);
        }
        return "redirect:/books/shelf";
    }

}
