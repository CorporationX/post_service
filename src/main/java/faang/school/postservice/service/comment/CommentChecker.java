package faang.school.postservice.service.comment;

import faang.school.postservice.exception.comment.IndexSearcherException;
import faang.school.postservice.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

@Component
@Slf4j
public class CommentChecker {

    private final ByteBuffersDirectory buffersDirectory;

    public CommentChecker(ModerationDictionary moderationDictionary) {
        this.buffersDirectory = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        try {
            IndexWriter indexWriter = new IndexWriter(buffersDirectory, config);
            for (String word : moderationDictionary.getObsceneWords()) {
                Document document = new Document();
                document.add(new TextField("word", word.toLowerCase(), Field.Store.YES));
                indexWriter.addDocument(document);
            }
            indexWriter.commit();
            log.info("Finished indexing moderation dictionary");
        } catch (IOException e) {
            throw new IndexSearcherException("Error to initializing lucene index", e);
        }
    }

    public boolean isAcceptableComment(Comment comment) {
        Set<String> tokenizer = tokenizeComment(comment);
        try (DirectoryReader reader = DirectoryReader.open(buffersDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            for (String word : tokenizer) {
                Query query = new FuzzyQuery(new Term("word", word), 2);
                ScoreDoc[] hits = searcher.search(query, 1).scoreDocs;
                if (hits.length > 0) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new IndexSearcherException(String.format("Error while searching bad words in comment " +
                    "with id %d", comment.getId()), e);
        }
        return true;
    }

    private Set<String> tokenizeComment(Comment comment) {
        StringTokenizer tokenizer = new StringTokenizer(comment.getContent(), " ,.!?;:()[]{}\"'\\/\n\t\r");
        Set<String> words = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            words.add(tokenizer.nextToken().toLowerCase());
        }
        return words;
    }
}
