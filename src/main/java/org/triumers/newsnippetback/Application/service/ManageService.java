package org.triumers.newsnippetback.Application.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.triumers.newsnippetback.domain.aggregate.entity.Category;
import org.triumers.newsnippetback.domain.aggregate.entity.CrawlingQuiz;
import org.triumers.newsnippetback.domain.aggregate.entity.Quiz;
import org.triumers.newsnippetback.domain.dto.CrawlingQuizDTO;
import org.triumers.newsnippetback.domain.dto.QuizDTO;
import org.triumers.newsnippetback.domain.repository.CategoryRepository;
import org.triumers.newsnippetback.domain.repository.CrawlingQuizRepository;
import org.triumers.newsnippetback.domain.repository.QuizRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ManageService {

    private final QuizRepository quizRepository;
    private final CrawlingQuizRepository crawlingQuizRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;

    @Autowired
    public ManageService(QuizRepository quizRepository, CrawlingQuizRepository crawlingQuizRepository,
                         CategoryRepository categoryRepository, ModelMapper mapper) {
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
                Category category = categoryRepository.findById(crawlingQuizList.get(i).getCategoryId())
                        .orElseThrow(IllegalAccessError::new);
                crawlingQuizDTOList.get(i).setCategory(category);
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

            Category category = categoryRepository.findById(crawlingQuiz.getCategoryId())
                    .orElseThrow(IllegalAccessError::new);
            crawlingQuizDTO.setCategory(category);

            return crawlingQuizDTO;
        }
        throw new IllegalAccessError("문제 정보를 불러올 수 없음");
    }

    @Transactional
    public List<Quiz> insertSelectedQuiz(List<CrawlingQuizDTO> crawlingQuizDTOList) {

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<Quiz> quizList = crawlingQuizDTOList.stream()
                .map(crawlingQuizDTO -> mapper.map(crawlingQuizDTO, Quiz.class))
                .collect(Collectors.toList());

        int index = getMaxIndex() + 1;
        for (int i = 0; i < crawlingQuizDTOList.size(); i++) {

            CrawlingQuizDTO seletedQuiz = crawlingQuizDTOList.get(i);

            quizList.get(i).setNo(index++);
            quizList.get(i).setDate(LocalDate.now().plusDays(1));
            quizList.get(i).setCategoryId(seletedQuiz.getCategory().getId());
            quizList.get(i).setOriginQuizId(seletedQuiz.getId());
        }

        return quizRepository.saveAll(quizList);
    }

    public int getMaxIndex() {
        return quizRepository.countByDate(LocalDate.now().plusDays(1));
    }

    public List<QuizDTO> selectQuizListByDate(LocalDate date) {
        List<Quiz> quizList = quizRepository.findByDateOrderByNoAsc(date);

        if (!quizList.isEmpty()) {
            List<QuizDTO> quizDTOList = quizList.stream()
                    .map(quiz -> mapper.map(quiz, QuizDTO.class))
                    .collect(Collectors.toList());

            for (int i = 0; i < quizList.size(); i++) {
                Category category = categoryRepository.findById(quizList.get(i).getCategoryId())
                        .orElseThrow(IllegalAccessError::new);
                quizDTOList.get(i).setCategory(category);
            }
            return quizDTOList;
        }
        throw new NoSuchElementException("문제 정보를 불러올 수 없음");
    }

    @Transactional
    public QuizDTO deleteQuizInListById(int id) {
        Quiz deleteQuiz = quizRepository.findById(id).orElseThrow(IllegalAccessError::new);

        if (deleteQuiz != null) {

            quizRepository.deleteById(id);
            List<Quiz> modifyQuizList = quizRepository
                    .findByDateAndNoGreaterThanOrderByNoAsc(LocalDate.now().plusDays(1), deleteQuiz.getNo());

            for (Quiz modifyQuiz : modifyQuizList) {
                modifyQuiz.setNo(modifyQuiz.getNo() - 1);
            }
            return mapper.map(deleteQuiz, QuizDTO.class);
        }
        throw new IllegalAccessError("문제 정보를 불러올 수 없음");
    }
}
