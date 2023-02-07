package org.example.app.services;

import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class BookRepository implements ProjectRepository<Book> {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    private final List<Book> repo = new ArrayList<>();

    @Override
    public List<Book> retreiveAll() {
        return new ArrayList<>(repo);
    }

    @Override
    public void store(Book book) {
        book.setId(book.hashCode());
        logger.info("store new book: " + book);
        repo.add(book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        for (Book book : retreiveAll()) {
            if (book.getId().equals(bookIdToRemove)) {
                logger.info("remove book completed: " + book);
                return repo.remove(book);
            }
        }
        return false;
    }

    @Override
    public boolean removeItemByRegex(String queryRegex) {
        boolean isFound = false;
        Pattern pattern = Pattern.compile(queryRegex);
        for (Book book : retreiveAll()) {
            Matcher matcher1 = pattern.matcher(book.getAuthor());
            Matcher matcher2 = pattern.matcher(book.getTitle());
            Matcher matcher3 = pattern.matcher(String.valueOf(book.getSize()));
            if (matcher1.find() || matcher2.find() || matcher3.find()) {
                isFound = true;
                logger.info("remove book (by Regex " + queryRegex + ") completed: " + book);
                repo.remove(book);
            }
        }
        return isFound;
    }
}
