package org.triumers.newsnippetback.Application.controller;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.triumers.newsnippetback.Application.service.UserService;
import org.triumers.newsnippetback.common.exception.UserNotFoundException;
import org.triumers.newsnippetback.domain.aggregate.enums.UserRole;
import org.triumers.newsnippetback.domain.aggregate.enums.UserStatus;
import org.triumers.newsnippetback.domain.aggregate.vo.ResponseLeagueVO;
import org.triumers.newsnippetback.domain.aggregate.vo.ResponseUserInfoVO;
import org.triumers.newsnippetback.Application.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseUserInfoVO> findUserByEmail(@PathVariable("email") String email) {

        try {
            UserDTO userDTO = userService.findUserByEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOToUserInfoVO(userDTO));

        } catch (UserNotFoundException e) {
            ResponseUserInfoVO response = new ResponseUserInfoVO();
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseUserInfoVO> findUserById(@PathVariable("id") int id) {

        try {
            UserDTO userDTO = userService.findUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOToUserInfoVO(userDTO));

        } catch (UserNotFoundException e) {
            ResponseUserInfoVO response = new ResponseUserInfoVO();
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<ResponseUserInfoVO> findUserByNickname(@PathVariable("nickname") String nickname) {
        System.out.println("nickname = " + nickname);
        try {
            UserDTO userDTO = userService.findUserByNickname(nickname);
            return ResponseEntity.status(HttpStatus.OK).body(userDTOToUserInfoVO(userDTO));

        } catch (UserNotFoundException e) {
            ResponseUserInfoVO response = new ResponseUserInfoVO();
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/my-page")
    public ResponseEntity<ResponseUserInfoVO> myPage() {
        try {
            UserDTO userDTO = userService.findByToken();
            return ResponseEntity.status(HttpStatus.OK).body(userDTOToUserInfoVO(userDTO));

        } catch (ExpiredJwtException e) {
            ResponseUserInfoVO response = new ResponseUserInfoVO();
            response.setMessage("[ERROR] 로그인 이후 이용해주십시오.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UserNotFoundException e) {
            ResponseUserInfoVO response = new ResponseUserInfoVO();
            response.setMessage(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/league")
    public ResponseEntity<ResponseLeagueVO> findLeague() {

        ResponseLeagueVO response = new ResponseLeagueVO();
        try {
            List<UserDTO> userDTOs = userService.findLeague();
            List<ResponseLeagueVO.Ranker> rankers = new ArrayList<>();

            int rank = 1;

            for (UserDTO user : userDTOs) {
                rankers.add(new ResponseLeagueVO.Ranker(rank, user.getNickname(), user.getCorrectCnt()));
                rank++;
            }

            response.setRankers(rankers);
            response.setMessage("조회 성공");

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UserNotFoundException e) {
            response.setMessage("[Error] 조회 실패");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseUserInfoVO userDTOToUserInfoVO(UserDTO user) {
        return new ResponseUserInfoVO("조회 성공", user.getName(), user.getNickname(), user.getEmail(),
                userRoleToString(user.getUserRole()), userStatusToString(user.getUserStatus()), user.getSolvedCnt(),
                user.getCorrectCnt(), user.getRank());
    }

    private String userRoleToString(UserRole userRole) {
        if (userRole.equals(UserRole.USER)) {
            return "일반 회원";
        }
        if (userRole.equals(UserRole.MANAGER)) {
            return "운영자";
        }
        return "관리자";
    }

    private String userStatusToString(UserStatus userStatus) {
        if (userStatus.equals(UserStatus.ACTIVE)) {
            return "활성 회원";
        }
        if (userStatus.equals(UserStatus.BLOCKED)) {
            return "차단 회원";
        }
        if (userStatus.equals(UserStatus.DORMANT)) {
            return "휴면 회원";
        }
        return "탈퇴 회원";
    }
}
