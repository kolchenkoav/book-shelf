package org.example.app.services;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;



import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class BookRepository implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    //private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BookRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum)->{
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;
        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author", book.getAuthor());
        parameterSource.addValue("title", book.getTitle());
        parameterSource.addValue("size", book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author, :title, :size)", parameterSource);
        logger.info("store new book: " + book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id", parameterSource);
        logger.info("remove book completed");
        return true;
    }

    @Override
    public boolean removeItemByRegex(@NotNull String queryRegex) {
        if (StringUtils.isEmpty(queryRegex)) {
            logger.info("Regex is empty...");
            return false;
        }

        boolean isFound = false;
        Pattern pattern = Pattern.compile(queryRegex);
        for (Book book : retreiveAll()) {
            Matcher matcher1 = pattern.matcher(book.getAuthor());
            Matcher matcher2 = pattern.matcher(book.getTitle());
            Matcher matcher3 = pattern.matcher(String.valueOf(book.getSize()));
            boolean b1 = matcher1.find();
            boolean b2 = matcher2.find();
            boolean b3 = matcher3.find();
            isFound = (b1 || b2 || b3);

            /*
            *   Реализовано 3 условия потому что REGEX может быть установлен СРАЗУ на несколько полей
            *
            * */
            if (isFound) {
                logger.info("*** => remove book (by Regex " + queryRegex + ") completed: " + book + " b1="+b1+" b2="+b2+" b3="+b3);
                if (b1) {
                    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
                    parameterSource.addValue("value", queryRegex);
                    jdbcTemplate.update("DELETE FROM books WHERE author REGEXP :value", parameterSource);
                }
                if (b2) {
                    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
                    parameterSource.addValue("value", queryRegex);
                    jdbcTemplate.update("DELETE FROM books WHERE title REGEXP :value", parameterSource);
                }
                if (b3) {
                    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
                    parameterSource.addValue("value", queryRegex);
                    jdbcTemplate.update("DELETE FROM books WHERE size REGEXP :value", parameterSource);
                }
                logger.info("remove book completed");
            }
        }
        return isFound;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    private void defaultInit() {
        logger.info("default INIT in book repo bean");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in book repo bean");
    }
}
