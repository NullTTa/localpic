package com.localpick.config;

import com.localpick.domain.comment.CommentService;
import com.localpick.domain.follow.FollowService;
import com.localpick.domain.hashtag.HashtagService;
import com.localpick.domain.insight.PostViewLog;
import com.localpick.domain.insight.PostViewLogRepository;
import com.localpick.domain.like.LikeService;
import com.localpick.domain.member.AccountType;
import com.localpick.domain.member.Member;
import com.localpick.domain.member.MemberRepository;
import com.localpick.domain.post.Post;
import com.localpick.domain.post.PostRepository;
import com.localpick.domain.post.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * dev 프로필에서만 실행되는 샘플 데이터.
 * 모든 계정 비밀번호: password123
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostViewLogRepository viewLogRepository;
    private final HashtagService hashtagService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final FollowService followService;
    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        if (memberRepository.count() > 0) return;
        String pw = new BCryptPasswordEncoder().encode("password123");

        Member cafe = store("cafe@localpick.com", pw, "오늘의원두", "카페", "인천 부평구 부평동",
                "매일 아침 직접 로스팅하는 동네 카페 ☕ 부평역 3번 출구 도보 5분", "/seed/profile_cafe.png");
        Member bakery = store("bakery@localpick.com", pw, "빵굽는마을", "베이커리", "인천 부평구 십정동",
                "새벽 5시부터 굽는 천연발효종 빵집 🥐 오전 11시 소금빵 나와요", "/seed/profile_bakery.png");
        Member flower = store("flower@localpick.com", pw, "플로랄데이", "꽃집", "인천 남동구 구월동",
                "기념일 꽃다발 · 원데이 클래스 💐 예약은 DM", "/seed/profile_flower.png");
        Member ramen = store("ramen@localpick.com", pw, "십정동라멘", "음식점", "인천 부평구 십정동",
                "돈코츠 라멘 전문 🍜 매주 월요일 휴무", "/seed/profile_ramen.png");

        Member yeongho = person("yeongho@localpick.com", pw, "영호", "부평 맛집·카페 기록 중", "/seed/profile_user1.png");
        Member minsu = person("minsu@localpick.com", pw, "민수", "빵 덕후 🍞", "/seed/profile_user2.png");
        Member seoyeon = person("seoyeon@localpick.com", pw, "서연", "주말엔 꽃 시장", "/seed/profile_user3.png");
        Member jihun = person("jihun@localpick.com", pw, "지훈", "라멘은 진리", "/seed/profile_user4.png");

        LocalDate today = LocalDate.now();
        List<Post> posts = new ArrayList<>();
        posts.add(post(cafe, PostType.EVENT, "/seed/post_cafe_event.png",
                "🎉 오픈 2주년 이벤트! 이번 주 아메리카노 1+1 \n게시물 보여주시면 쿠키도 서비스로 드려요 #부평카페 #오픈이벤트 #아메리카노",
                today.minusDays(2), today.plusDays(5), "https://map.naver.com"));
        posts.add(post(bakery, PostType.PROMOTION, "/seed/post_bakery_new.png",
                "신메뉴 출시 🥐 버터 듬뿍 '앙버터 소금빵' 오늘부터 판매합니다. 하루 60개 한정! #십정동빵집 #소금빵 #신메뉴",
                null, null, "https://smartstore.naver.com"));
        posts.add(post(yeongho, PostType.DAILY, "/seed/post_user_cafe.png",
                "오늘의원두 라떼 진짜 고소하다… 창가 자리 햇살 최고 #부평카페 #카페투어", null, null, null));
        posts.add(post(flower, PostType.EVENT, "/seed/post_flower_class.png",
                "11월 원데이 꽃꽂이 클래스 모집 💐 선착순 8명, 재료비 포함 35,000원 #꽃꽂이클래스 #구월동꽃집 #원데이클래스",
                today, today.plusDays(10), null));
        posts.add(post(ramen, PostType.PROMOTION, "/seed/post_ramen.png",
                "날씨가 쌀쌀해졌죠? 진한 돈코츠 국물 한 그릇 하고 가세요. 점심 세트 9,900원 #십정동맛집 #라멘 #점심메뉴",
                null, null, "https://map.naver.com"));
        posts.add(post(minsu, PostType.DAILY, "/seed/post_user_bread.png",
                "빵굽는마을 소금빵 오픈런 성공 🏃 겉바속촉 그 자체 #소금빵 #십정동빵집", null, null, null));
        posts.add(post(cafe, PostType.PROMOTION, "/seed/post_cafe_bean.png",
                "이번 달 원두는 에티오피아 예가체프 🌸 산미 좋아하시는 분들 꼭 드셔보세요 #부평카페 #핸드드립 #원두추천",
                null, null, null));
        posts.add(post(seoyeon, PostType.DAILY, "/seed/post_user_flower.png",
                "플로랄데이에서 엄마 생신 꽃다발 💐 색감 너무 예쁘게 잡아주셨다 #구월동꽃집 #꽃다발", null, null, null));
        posts.add(post(bakery, PostType.EVENT, "/seed/post_bakery_event.png",
                "단골 감사 이벤트 🍞 스탬프 10개 모으면 식빵 한 봉지 증정! #십정동빵집 #스탬프이벤트",
                today.minusDays(1), today.plusDays(20), null));
        posts.add(post(jihun, PostType.DAILY, "/seed/post_user_ramen.png",
                "십정동라멘 차슈 두께 실화냐 🍜 #라멘 #십정동맛집", null, null, null));
        posts.add(post(ramen, PostType.EVENT, "/seed/post_ramen_event.png",
                "지난 주말 진행한 '라멘 한 그릇 리뷰 이벤트' 종료되었습니다. 참여해주신 분들 감사합니다! #라멘 #리뷰이벤트",
                today.minusDays(14), today.minusDays(7), null));
        posts.add(post(cafe, PostType.DAILY, "/seed/post_cafe_daily.png",
                "비 오는 날엔 따뜻한 바닐라 라떼 한 잔 어떠세요 ☔ #부평카페 #바닐라라떼", null, null, null));

        // 게시물 작성일을 최근 열흘에 흩뿌린다
        for (int i = 0; i < posts.size(); i++) {
            jdbc.update("update post set created_at = ? where post_id = ?",
                    LocalDateTime.now().minusDays(posts.size() - 1 - i).minusHours(i), posts.get(i).getId());
        }

        // 팔로우
        Member[] people = {yeongho, minsu, seoyeon, jihun};
        for (Member p : people) {
            followService.toggle(p.getId(), cafe.getId());
            followService.toggle(p.getId(), bakery.getId());
        }
        followService.toggle(yeongho.getId(), ramen.getId());
        followService.toggle(jihun.getId(), ramen.getId());
        followService.toggle(seoyeon.getId(), flower.getId());
        followService.toggle(yeongho.getId(), minsu.getId());
        followService.toggle(bakery.getId(), cafe.getId());
        // 팔로우 날짜도 최근 7일에 나눠 둔다 (인사이트 그래프용)
        jdbc.update("update follow set created_at = dateadd('DAY', -mod(follow_id, 7), current_timestamp)");

        // 좋아요 · 댓글
        Random random = new Random(7);
        Member[] all = {cafe, bakery, flower, ramen, yeongho, minsu, seoyeon, jihun};
        for (Post post : posts) {
            for (Member m : all) {
                if (!m.getId().equals(post.getWriter().getId()) && random.nextInt(10) < 6) {
                    likeService.toggle(post.getId(), m.getId());
                }
            }
        }
        commentService.write(posts.get(0).getId(), yeongho.getId(), "내일 친구랑 갈게요! 쿠키도 기대됩니다 😆");
        commentService.write(posts.get(0).getId(), minsu.getId(), "1+1 주말에도 되나요?");
        commentService.write(posts.get(0).getId(), cafe.getId(), "네! 이벤트 기간 내내 주말 포함입니다 🙌");
        commentService.write(posts.get(1).getId(), minsu.getId(), "앙버터 소금빵이라니… 내일 오픈런 각");
        commentService.write(posts.get(1).getId(), seoyeon.getId(), "예약도 되나요?");
        commentService.write(posts.get(3).getId(), seoyeon.getId(), "신청하고 싶어요! DM 드릴게요");
        commentService.write(posts.get(4).getId(), jihun.getId(), "점심 세트 가성비 최고");
        commentService.write(posts.get(2).getId(), cafe.getId(), "창가 자리 좋아해주셔서 감사해요 ☀️");

        // 조회 로그 (최근 7일) → 조회수 컬럼에 반영
        for (Post post : posts) {
            int views = 15 + random.nextInt(60);
            for (int v = 0; v < views; v++) {
                Member viewer = all[random.nextInt(all.length)];
                viewLogRepository.save(new PostViewLog(post.getId(), viewer.getId(),
                        LocalDateTime.now().minusDays(random.nextInt(7)).minusMinutes(random.nextInt(600))));
            }
        }
        jdbc.update("update post p set view_count = (select count(*) from post_view_log v where v.post_id = p.post_id)");

        log.info("샘플 데이터 생성 완료: 회원 {}명, 게시물 {}개", all.length, posts.size());
    }

    private Member store(String email, String pw, String nickname, String category, String location,
                         String bio, String image) {
        return memberRepository.save(Member.builder().email(email).password(pw).nickname(nickname)
                .accountType(AccountType.BUSINESS).businessCategory(category).location(location)
                .bio(bio).profileImage(image).build());
    }

    private Member person(String email, String pw, String nickname, String bio, String image) {
        return memberRepository.save(Member.builder().email(email).password(pw).nickname(nickname)
                .accountType(AccountType.PERSONAL).bio(bio).profileImage(image).build());
    }

    private Post post(Member writer, PostType type, String image, String content,
                      LocalDate start, LocalDate end, String link) {
        Post post = postRepository.save(Post.builder().writer(writer).postType(type).imageUrl(image)
                .content(content).eventStartDate(start).eventEndDate(end).linkUrl(link).build());
        hashtagService.syncTags(post);
        return post;
    }
}
