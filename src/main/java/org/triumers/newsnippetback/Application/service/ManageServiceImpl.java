package org.triumers.newsnippetback.Application.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.triumers.newsnippetback.domain.aggregate.entity.Category;
import org.triumers.newsnippetback.domain.aggregate.entity.CrawlingQuiz;
import org.triumers.newsnippetback.domain.aggregate.entity.Quiz;
import org.triumers.newsnippetback.Application.dto.CrawlingQuizDTO;
import org.triumers.newsnippetback.Application.dto.QuizDTO;
import org.triumers.newsnippetback.domain.repository.CategoryRepository;
import org.triumers.newsnippetback.domain.repository.CrawlingQuizRepository;
import org.triumers.newsnippetback.domain.repository.QuizRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ManageServiceImpl implements ManageService {

    private final QuizRepository quizRepository;
    private final CrawlingQuizRepository crawlingQuizRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    final static LocalDate nextDate = LocalDate.now().plusDays(1);

    @Autowired
    public ManageServiceImpl(QuizRepository quizRepository, CrawlingQuizRepository crawlingQuizRepository, CategoryRepository categoryRepository, ModelMapper mapper) {
        this.quizRepository = quizRepository;
        this.crawlingQuizRepository = crawlingQuizRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<CrawlingQuizDTO> selectCrawlingQuizListByDate(LocalDate date) {

        List<CrawlingQuiz> crawlingQuizList = crawlingQuizRepository.findByNewsDate(date);

        if (!crawlingQuizList.isEmpty()) {
            List<CrawlingQuizDTO> crawlingQuizDTOList = crawlingQuizList.stream()
                    .map(crawlingQuiz -> mapper.map(crawlingQuiz, CrawlingQuizDTO.class))
                    .collect(Collectors.toList());

            for (int i = 0; i < crawlingQuizList.size(); i++) {
                CrawlingQuiz crawlingQuiz = crawlingQuizList.get(i);
                Category category = categoryRepository.findById(crawlingQuiz.getCategoryId());
                crawlingQuizDTOList.get(i).setCategory(category);

                boolean isSelected = quizRepository.countByDateAndOriginQuizId
                        (nextDate, crawlingQuiz.getId()) > 0;
                crawlingQuizDTOList.get(i).setSelected(isSelected);
            }
            return crawlingQuizDTOList;
        } else {
            // 이후에 크롤링 서버에 문제 생성 요청하기
            throw new NoSuchElementException("문제 정보를 불러올 수 없음");
        }
    }

    public CrawlingQuizDTO selectCrawlingQuizByID(int id) {
        CrawlingQuiz crawlingQuiz = crawlingQuizRepository.findById(id).orElseThrow(IllegalAccessError::new);

        if (crawlingQuiz != null) {
            CrawlingQuizDTO crawlingQuizDTO = mapper.map(crawlingQuiz, CrawlingQuizDTO.class);

            Category category = categoryRepository.findById(crawlingQuiz.getCategoryId());
            crawlingQuizDTO.setCategory(category);

            return crawlingQuizDTO;
        }
        throw new IllegalAccessError("문제 정보를 불러올 수 없음");
    }

    @Transactional
    public Quiz insertSelectedQuizById(int id) {

        CrawlingQuizDTO selectedQuiz = selectCrawlingQuizByID(id);
        Quiz insertQuiz = mapper.map(selectedQuiz, Quiz.class);

        insertQuiz.setId(null);
        insertQuiz.setNo(getMaxNo() + 1);
        insertQuiz.setDate(nextDate);
        insertQuiz.setCategoryId(selectedQuiz.getCategory().getId());
        insertQuiz.setOriginQuizId(selectedQuiz.getId());

        return quizRepository.save(insertQuiz);
    }

    public int getMaxNo() {
        return quizRepository.countByDate(nextDate);
    }

    public List<QuizDTO> selectQuizListByDate(LocalDate date) {
        List<Quiz> quizList = quizRepository.findByDateOrderByNoAsc(date);

        if (!quizList.isEmpty()) {
            List<QuizDTO> quizDTOList = quizList.stream()
                    .map(quiz -> mapper.map(quiz, QuizDTO.class))
                    .collect(Collectors.toList());

            for (int i = 0; i < quizList.size(); i++) {
                Category category = categoryRepository.findById(quizList.get(i).getCategoryId());
                quizDTOList.get(i).setCategory(category);
            }
            return quizDTOList;
        }
        throw new NoSuchElementException("문제 정보를 불러올 수 없음");
    }

    @Transactional
    public QuizDTO deleteQuizInListById(int id) {

        Quiz deleteQuiz = quizRepository.findByOriginQuizIdAndDate(id, nextDate);

        if (deleteQuiz != null) {

            quizRepository.deleteById(deleteQuiz.getId());
            List<Quiz> modifyQuizList = quizRepository
                    .findByDateAndNoGreaterThanOrderByNoAsc(nextDate, deleteQuiz.getNo());

            for (Quiz modifyQuiz : modifyQuizList) {
                modifyQuiz.setNo(modifyQuiz.getNo() - 1);
            }
            return mapper.map(deleteQuiz, QuizDTO.class);
        }
        throw new IllegalAccessError("문제 정보를 불러올 수 없음");
    }
}
