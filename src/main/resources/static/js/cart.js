$(document).ready(function(){
    // CSRF 토큰 설정
    var csrfToken = $('meta[name="_csrf"]').attr('content');
    var csrfHeader = $('meta[name="_csrf_header"]').attr('content');

    if(csrfToken && csrfHeader){
        $.ajaxSetup({
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        });
        console.log("CSRF 토큰 설정 완료");
    } else {
        console.warn("CSRF 토큰 또는 헤더 이름이 설정되지 않았습니다.");
    }

    // 필터링 버튼 클릭 시
    $("#filterButton").click(function() {
        console.log("필터링 버튼 클릭됨");
        var selectedDepartment = $("#departmentFilter").val();
        var selectedClassification = $("#classificationFilter").val();

        // 강의 목록 테이블만 필터링 대상
        $("#coursesTable tbody tr").each(function() {
            var dept = $(this).find("td:nth-child(3)").text(); // 학과 (3번째 컬럼)
            var classification = $(this).find("td:nth-child(4)").text(); // 분류 (4번째 컬럼)
            var show = true;

            if(selectedDepartment && dept !== selectedDepartment) {
                show = false;
            }
            if(selectedClassification && classification !== selectedClassification) {
                show = false;
            }

            if(show) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    // 장바구니 추가 버튼 클릭 시
    $(document).on('click', '.btn-add-cart', function(){
        var courseId = $(this).data("course-id");
        var classId = $(this).data("class-id");
        var button = $(this);

        // classId 유효성 검사
        if(!classId){
            alert("유효한 클래스를 선택해주세요.");
            return;
        }

        // 버튼 일시적으로 비활성화
        button.prop("disabled", true).text("처리 중...");

        $.ajax({
            url: "/cart/add",
            type: "POST",
            data: {
                courseId: courseId,
                classId: classId
            },
            success: function(response){
                console.log("장바구니 추가 응답:", response); // 추가된 부분
                if(response.status === "success") {
                    alert("장바구니에 성공적으로 추가되었습니다.");
                    // 장바구니 목록 업데이트
                    addCartRow(response.cart); // response.cart가 정상적으로 전달됨
                    // 장바구니에 추가된 강의의 "장바구니 추가" 버튼 비활성화
                    disableAddCartButton(courseId);
                } else if(response.status === "unauthenticated") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/login";
                } else {
                    alert("장바구니 추가 실패: " + response.message);
                }
                // 버튼 상태 복원
                button.prop("disabled", false).text("장바구니 추가");
            },
            error: function(xhr){
                alert("장바구니 추가 중 오류가 발생했습니다.");
                // 버튼 상태 복원
                button.prop("disabled", false).text("장바구니 추가");
            }
        });
    });

    // 장바구니에서 수강신청 버튼 클릭 시
    $(document).on('click', '.btn-enroll-from-cart', function(){
        var courseId = $(this).data("course-id");
        var classId = $(this).data("class-id");
        var cartId = $(this).data("cart-id"); // 추가된 부분
        var button = $(this);

        // 버튼 일시적으로 비활성화
        button.prop("disabled", true).text("처리 중...");

        $.ajax({
            url: "/enroll/add",
            type: "POST",
            data: {
                courseId: courseId,
                classId: classId
            },
            success: function(response){
                console.log("수강신청 응답:", response);
                if(response.status === "success") {
                    alert("수강신청이 성공적으로 완료되었습니다.");
                    // 수강신청 목록에 추가
                    addEnrollmentRow(response.enrollment);
                    // 장바구니에서 해당 강의 제거
                    removeCartRow(cartId);
                    // 장바구니에 추가된 강의의 "장바구니 추가" 버튼 비활성화
                    disableAddCartButton(courseId);
                } else if(response.status === "unauthenticated") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/login";
                } else {
                    alert("수강신청 실패: " + response.message);
                }
                // 버튼 상태 복원
                button.prop("disabled", false).text("수강신청");
            },
            error: function(xhr){
                alert("수강신청 중 오류가 발생했습니다.");
                // 버튼 상태 복원
                button.prop("disabled", false).text("수강신청");
            }
        });
    });

    // 장바구니 삭제 버튼 클릭 시
    $(document).on('click', '.btn-remove-cart', function(){
        var cartId = $(this).data("cart-id");
        var button = $(this);
        var courseId = $(this).data("course-id"); // 수정된 부분: data-course-id 사용

        if(confirm("장바구니에서 삭제하시겠습니까?")) {
            // 버튼 일시적으로 비활성화
            button.prop("disabled", true).text("처리 중...");

            $.ajax({
                url: "/cart/remove",
                type: "POST",
                data: { cartId: cartId },
                success: function(response){
                    if(response.status === "success") {
                        alert("장바구니에서 성공적으로 삭제되었습니다.");
                        // Remove the row from the table
                        removeCartRow(cartId);
                        // 장바구니에 추가된 강의의 "장바구니 추가" 버튼 활성화
                        enableAddCartButton(courseId);
                    } else if(response.status === "unauthenticated") {
                        alert("로그인이 필요합니다.");
                        window.location.href = "/login";
                    } else {
                        alert("삭제 실패: " + response.message);
                        // 버튼 상태 복원
                        button.prop("disabled", false).text("삭제");
                    }
                },
                error: function(){
                    alert("삭제 중 오류가 발생했습니다.");
                    // 버튼 상태 복원
                    button.prop("disabled", false).text("삭제");
                }
            });
        }
    });

    // 수강취소 버튼 클릭 시
    $(document).on('click', '.btn-remove-enrollment', function(){
        var enrollmentId = $(this).data("enrollment-id");
        var button = $(this);
        var courseId = $(this).data("course-id"); // 수정된 부분: data-course-id 사용

        if(confirm("신청한 강의를 삭제하시겠습니까?")) {
            // 버튼 일시적으로 비활성화
            button.prop("disabled", true).text("처리 중...");

            $.ajax({
                url: "/enroll/remove",
                type: "POST",
                data: { enrollmentId: enrollmentId },
                success: function(response){
                    if(response.status === "success") {
                        alert("수강신청이 성공적으로 취소되었습니다.");
                        // Remove the row from the table
                        removeEnrollmentRow(enrollmentId);
                        // "장바구니 추가" 버튼 활성화
                        enableAddCartButton(courseId);
                    } else if(response.status === "unauthenticated") {
                        alert("로그인이 필요합니다.");
                        window.location.href = "/login";
                    } else {
                        alert("취소 실패: " + response.message);
                        // 버튼 상태 복원
                        button.prop("disabled", false).text("삭제");
                    }
                },
                error: function(){
                    alert("취소 중 오류가 발생했습니다.");
                    // 버튼 상태 복원
                    button.prop("disabled", false).text("삭제");
                }
            });
        }
    });

    // 장바구니에 추가된 강의를 장바구니 목록에 추가하는 함수
    function addCartRow(cart) {
        var row = `<tr id="cart-row-${cart.cartId}">
                    <td>
                        <button class="btn btn-sm btn-success btn-enroll-from-cart"
                                data-class-id="${cart.classId}"
                                data-course-id="${cart.courseId}"
                                data-cart-id="${cart.cartId}"
                                title="장바구니에서 바로 수강 신청합니다."
                                type="button">수강신청
                        </button>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-danger btn-remove-cart"
                                data-cart-id="${cart.cartId}"
                                data-course-id="${cart.courseId}"
                                title="장바구니에서 삭제합니다."
                                type="button">삭제
                        </button>
                    </td>
                    <td>${cart.courseName}</td>
                    <td>${cart.departmentName}</td>
                    <td>${cart.classification}</td>
                    <td>${cart.semester}</td>
                    <td>${cart.credit}</td>
                    <td>${cart.professorName}</td>
                    <td>${cart.roomNo}</td>
                    <td>${cart.dayOfWeek} (${cart.startTime}-${cart.endTime})</td>
                </tr>`;
        $("#cartCoursesTable tbody").append(row);
    }

    // 장바구니에서 수강신청 시 신청된 강의 목록에 추가하는 함수
    function addEnrollmentRow(enrollment) {
        var row = `<tr id="enrollment-row-${enrollment.enrollmentId}">
                    <td>
                        <button class="btn btn-sm btn-danger btn-remove-enrollment"
                                data-enrollment-id="${enrollment.enrollmentId}"
                                data-course-id="${enrollment.courseId}" <
                                title="신청한 강의를 삭제합니다."
                                type="button">삭제
                        </button>
                    </td>
                    <td>${enrollment.courseName}</td>
                    <td>${enrollment.departmentName}</td>
                    <td>${enrollment.classification}</td>
                    <td>${enrollment.semester}</td>
                    <td>${enrollment.credit}</td>
                    <td>${enrollment.professorName}</td>
                    <td>${enrollment.roomNo}</td>
                    <td>${enrollment.dayOfWeek} (${enrollment.startTime}-${enrollment.endTime})</td>
                </tr>`;
        $("#enrolledCoursesTable tbody").append(row);
    }

    // "장바구니 추가" 버튼 비활성화 함수
    function disableAddCartButton(courseId) {
        $(`.btn-add-cart[data-course-id="${courseId}"]`).prop("disabled", true)
            .attr("title", "이미 장바구니에 추가된 강의입니다.")
            .addClass("btn-disabled");
    }

    // "장바구니 추가" 버튼 활성화 함수
    function enableAddCartButton(courseId) {
        $(`.btn-add-cart[data-course-id="${courseId}"]`).prop("disabled", false)
            .attr("title", "장바구니에 추가합니다.")
            .removeClass("btn-disabled");
    }

    // 장바구니 삭제 시 테이블 행 제거 함수
    function removeCartRow(cartId) {
        $(`#cart-row-${cartId}`).remove();
    }

    // 수강취소 시 테이블 행 제거 함수
    function removeEnrollmentRow(enrollmentId) {
        $(`#enrollment-row-${enrollmentId}`).remove();
    }
});
