package shop.hooking.hooking.global.constant;



public class ResponseConstant {

    /* Users */
    // login
    public static final String LOGIN_SUCCESS = "성공적으로 로그인되었습니다.";

    //tempPassword
    public static final String SEND_EMAIL_SUCCESS = "성공적으로 인증 코드를 전송했습니다.";
    public static final String CERTIFICATION_SUCCESS = "성공적으로 인증되었습니다.";


    // withdraw
    public static final String WITHDRAW_SUCCESS = "성공적으로 탈퇴되었습니다.";
    public static final String AVAILABLE_NICKNAME = "사용할 수 있는 닉네임입니다.";
    public static final String AVAILABLE_EMAIL = "사용할 수 있는 이메일입니다.";

    // signup
    public static final String SIGNUP_SUCCESS = "성공적으로 가입되었습니다.";

    //settings
    public static final String PASSWORD_CHANGE_SUCCESS = "성공적으로 비밀번호가 변경되었습니다.";

    // exception
    public static final String DUPLICATE_NICKNAME = "중복된 닉네임이 존재합니다.";
    public static final String DUPLICATE_EMAIL = "중복된 이메일이 존재합니다.";
    public static final String DUPLICATE_SCRAP = "중복된 스크랩이 존재합니다.";
    public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";
    public static final String NOTFOUND_USER = "해당 유저를 찾을 수 없습니다.";

    public static final String CARD_NOT_FOUND = "해당 결과를 찾을 수 없습니다.";

    public static final String PASSWORDS_NOT_EQUAL = "입력한 새 비밀번호가 서로 일치하지 않습니다.";
    public static final String BEFORE_PASSWORD_NOT_MATCH = "현재 비밀번호가 일치하지 않습니다.";

    public static final String CERTIFICATION_CODE_NOT_MATCH = "인증 코드가 일치하지 않습니다.";

    /* Friends */
    public static final String FRIEND_SUCCESS = "성공적으로 친구가 되었습니다.";
    public static final String DELETE_FRIEND_SUCCESS = "성공적으로 친구 끊기가 완료되었습니다.";
    public static final String CONFLICT_SELF_FRIEND = "자기 자신과 친구를 맺을 수 없습니다.";


    /* Image */
    public static final String SIZECHECK_FAILURE_IMAGE = "해당 이미지의 정보를 불러올 수 없습니다.";
    public static final String MISSING_IMAGE = "일부 이미지가 누락되었습니다. 다시 업로드해주세요.";

    /*Likes*/
    public static final String LIKES_ADD_SUCCESS = "좋아요가 추가되었습니다";
    public static final String LIKES_DELETE_SUCCESS = "좋아요가 취소되었습니다";

    /*Comment*/
    public static final String COMMENT_ADD_SUCCESS = "해당 댓글이 작성되었습니다.";
    public static final String COMMENT_DELETE_SUCCESS = "댓글이 삭제되었습니다.";
    public static final String NOTFOUND_PARENT = "상위 댓글이 존재하지 않습니다.";
    public static final String NO_AUTHORIZATION = "권한이 없습니다.";

    /* Token */
    public static final String BAD_TOKEN_REQUEST = "토큰을 확인하세요";
    public static final String EXPIRED_REFRESHTOKEN = "refresh 토큰이 만료되었습니다.";
    public static final String REISSUE_TOKEN_SUCCESS = "토큰이 재발급되었습니다.";

    /* SSE Exception */
    public static final String OUT_OF_INDEX = "허용가능한 인덱스 범위를 넘었습니다.";
}

