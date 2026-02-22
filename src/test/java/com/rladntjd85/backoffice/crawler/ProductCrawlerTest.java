package com.rladntjd85.backoffice.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

public class ProductCrawlerTest {

    @Test
    void crawlAndSaveAllToFile() throws Exception {
        String listUrl = "https://www.metree.co.kr/shop/big_section.php?cno1=1001";
        StringBuilder sqlBundle = new StringBuilder();

        // 0. 기존 데이터 초기화 쿼리 (상품 및 감사로그 초기화)
        sqlBundle.append("-- 기존 데이터 초기화\n");
        sqlBundle.append("SET FOREIGN_KEY_CHECKS = 0;\n");
        sqlBundle.append("TRUNCATE TABLE products;\n");
        sqlBundle.append("DELETE FROM audit_log WHERE target_type = 'PRODUCT';\n");
        sqlBundle.append("SET FOREIGN_KEY_CHECKS = 1;\n\n");

        try {
            Document listDoc = Jsoup.connect(listUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                    .get();

            Elements productBoxes = listDoc.select(".prd_basic > li");
            System.out.println("--- 크롤링 시작 (대상: " + productBoxes.size() + "개) ---");

            for (Element li : productBoxes) {
                try {
                    Element box = li.selectFirst(".box");
                    if (box == null) continue;

                    // 1. 기본 정보 추출
                    String name = box.select(".name a").text().trim();
                    if (name.isEmpty()) continue;

                    // 2. 카테고리 매칭
                    int categoryId = 1;
                    if (name.contains("스팀")) categoryId = 12;
                    else if (name.contains("수비드")) categoryId = 13;
                    else if (name.contains("훈제")) categoryId = 14;
                    else if (name.contains("큐브") || name.contains("한입")) categoryId = 15;
                    else if (name.contains("오븐")) categoryId = 16;
                    else if (name.contains("생닭") || name.contains("IQF")) categoryId = 17;

                    // 3. 가격 및 이미지
                    String priceStr = box.select(".sell").text().replaceAll("[^0-9]", "");
                    int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr);
                    String thumbUrl = box.select(".prdimg img").attr("abs:src");
                    String detailUrl = box.select(".prdimg a").attr("abs:href");

                    // 4. 상세 페이지 접속 및 이미지 태그만 추출
                    Document detailDoc = Jsoup.connect(detailUrl).userAgent("Mozilla/5.0").get();
                    Element contentsArea = detailDoc.selectFirst(".wing-detail-more-contents");
                    if (contentsArea == null) contentsArea = detailDoc.selectFirst(".detail_info");

                    StringBuilder imgTagsBuilder = new StringBuilder();
                    if (contentsArea != null) {
                        Elements detailImgs = contentsArea.select("img");
                        for (Element img : detailImgs) {
                            String src = img.attr("abs:src");
                            // 공지사항이나 불필요한 배너 제외 (선택 사항)
                            if (src.isEmpty() || src.contains("btn_")) continue;

                            // 이미지 태그 생성 (중앙 정렬 + lazy loading 추가)
                            imgTagsBuilder.append("<img src='").append(src)
                                    .append("' style='display:block; margin:0 auto; max-width:100%;' loading='lazy'><br>\n");
                        }
                    }
                    String finalContentHtml = imgTagsBuilder.toString().replace("'", "''");

                    // 5. 상품 INSERT 생성
                    String productSql = String.format(
                            "INSERT INTO products (name, category_id, price, stock, status, thumbnail_url, content, created_at, updated_at) " +
                                    "VALUES ('%s', %d, %d, 100, 'ACTIVE', '%s', '%s', NOW(), NOW());\n",
                            name.replace("'", "''"), categoryId, price, thumbUrl, finalContentHtml
                    );

                    // 6. 감사 로그 INSERT 생성
                    String auditSql = String.format(
                            "INSERT INTO audit_log (actor_user_id, action_type, target_type, target_id, ip, user_agent, diff_json, created_at) " +
                                    "VALUES (3, 'PRODUCT_CREATED', 'PRODUCT', (SELECT LAST_INSERT_ID()), '0:0:0:0:0:0:0:1', 'Crawler', '{\"snapshot\": {\"name\": \"%s\", \"price\": %d}}', NOW());\n",
                            name.replace("'", "''"), price
                    );

                    sqlBundle.append(productSql).append(auditSql).append("\n");
                    System.out.println("추출 성공: " + name);

                    Thread.sleep(800); // 속도 조절

                } catch (Exception e) {
                    System.err.println("상품 처리 중 오류 발생 (건너뜀): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("리스트 접속 중 오류 발생: " + e.getMessage());
        } finally {
            // 파일 저장 (에러가 나도 지금까지 수집한 건 저장)
            java.nio.file.Files.writeString(java.nio.file.Path.of("products_with_audit.sql"), sqlBundle.toString());
            System.out.println("--- 파일 저장 완료: products_with_audit.sql ---");
        }
    }

    @Test
    void crawlProteinCategory() throws Exception {
        // 1. 대상 URL 변경: 단백질 보충·간식
        String listUrl = "https://www.metree.co.kr/shop/big_section.php?cno1=1005";
        StringBuilder sqlBundle = new StringBuilder();

        // 초기화 쿼리 (카테고리 로그는 살리고 상품만!)
        sqlBundle.append("-- 단백질 카테고리 데이터 추가\n");

        try {
            Document listDoc = Jsoup.connect(listUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            Elements productBoxes = listDoc.select(".prd_basic > li");
            System.out.println("--- 단백질 카테고리 크롤링 시작 (대상: " + productBoxes.size() + "개) ---");

            for (Element li : productBoxes) {
                try {
                    Element box = li.selectFirst(".box");
                    if (box == null) continue;

                    String name = box.select(".name a").text().trim();

                    // 2. 카테고리 매칭 로직 (단백질 보충·간식 하위 ID)
                    int categoryId = 8; // 기본 (단백질 보충·간식 대분류)
                    if (name.contains("쉐이크") || name.contains("보충제")) categoryId = 34;
                    else if (name.contains("단백질 바") || name.contains("프로틴바")) categoryId = 35;
                    else if (name.contains("칩") || name.contains("스낵")) categoryId = 36;

                    String priceStr = box.select(".sell").text().replaceAll("[^0-9]", "");
                    int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr);
                    String thumbUrl = box.select(".prdimg img").attr("abs:src");
                    String detailUrl = box.select(".prdimg a").attr("abs:href");

                    // 상세 페이지 이미지 추출 (기존과 동일)
                    Document detailDoc = Jsoup.connect(detailUrl).userAgent("Mozilla/5.0").get();
                    Element contentsArea = detailDoc.selectFirst(".wing-detail-more-contents");
                    if (contentsArea == null) contentsArea = detailDoc.selectFirst(".detail_info");

                    StringBuilder imgTagsBuilder = new StringBuilder();
                    if (contentsArea != null) {
                        Elements detailImgs = contentsArea.select("img");
                        for (Element img : detailImgs) {
                            String src = img.attr("abs:src");
                            if (src.isEmpty() || src.contains("btn_")) continue;
                            imgTagsBuilder.append("<img src='").append(src)
                                    .append("' style='display:block; margin:0 auto; max-width:100%;' loading='lazy'><br>\n");
                        }
                    }

                    // SQL 생성
                    sqlBundle.append(String.format(
                            "INSERT INTO products (name, category_id, price, stock, status, thumbnail_url, content, created_at, updated_at) VALUES ('%s', %d, %d, 100, 'ACTIVE', '%s', '%s', NOW(), NOW());\n",
                            name.replace("'", "''"), categoryId, price, thumbUrl, imgTagsBuilder.toString().replace("'", "''")
                    ));
                    sqlBundle.append(String.format(
                            "INSERT INTO audit_log (actor_user_id, action_type, target_type, target_id, ip, user_agent, diff_json, created_at) VALUES (3, 'PRODUCT_CREATED', 'PRODUCT', (SELECT LAST_INSERT_ID()), '0:0:0:0:0:0:0:1', 'Crawler', '{\"snapshot\": {\"name\": \"%s\"}}', NOW());\n\n",
                            name.replace("'", "''")
                    ));

                    System.out.println("성공: " + name);
                    Thread.sleep(800);

                } catch (Exception e) {
                    System.err.println("실패: " + e.getMessage());
                }
            }
        } finally {
            java.nio.file.Files.writeString(java.nio.file.Path.of("protein_products.sql"), sqlBundle.toString());
            System.out.println("--- 파일 저장 완료: protein_products.sql ---");
        }
    }
}