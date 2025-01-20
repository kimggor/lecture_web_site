// src/main/resources/static/js/enrollment.js
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
        $("#coursesTable tbody tr").each(function() { // 변경된 부분
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

    // 수강신청 버튼 클릭 시
    $(".btn-enroll").click(function(){
        console.log("수강신청 버튼 클릭됨");
        var courseId = $(this).data("course-id");
        var classId = $(this).data("class-id");
        var button = $(this);

        console.log("수강신청 요청: courseId=" + courseId + ", classId=" + classId);

        $.ajax({
            url: "/enroll/add",
            type: "POST",
            data: { courseId: courseId, classId: classId },
            success: function(response){
                console.log("수강신청 응답:", response);
                if(response.status === "success") {
                    alert("수강신청이 완료되었습니다.");
                    // 현재 학점 업데이트
                    $("#currentCredits").text("현재 등록 학점: " + response.currentCredits + " 학점");
                    // 버튼 상태 변경
                    button.addClass("btn-disabled").prop("disabled", true).text("신청 완료");
                    // 신청한 강의 목록에 추가
                    var newRow = "<tr>" +
                        "<td>" +
                            "<button class='btn btn-sm btn-danger btn-remove-enrollment' data-enrollment-id='" + response.enrollmentId + "' title='신청한 강의를 삭제합니다.' type='button'>삭제</button>" +
                        "</td>" +
                        "<td>" + response.courseName + "</td>" +
                        "<td>" + response.departmentName + "</td>" +
                        "<td>" + response.classification + "</td>" +
                        "<td>" + response.semester + "</td>" +
                        "<td>" + response.credit + "</td>" +
                        "<td>" + response.professorName + "</td>" +
                        "<td>" + response.roomNo + "</td>" +
                        "<td>" + response.time + "</td>" +
                        "</tr>";
                    $("#enrolledCoursesTable tbody").append(newRow);

                    // 강의 목록 테이블의 신청 인원 업데이트
                    $("#coursesTable button.btn-enroll[data-course-id='" + response.courseId + "']")
                        .closest("tr")
                        .find("td:nth-child(11)") // 신청 인원은 11번째 컬럼
                        .text(response.updatedEnrolled); // 서버에서 받은 업데이트된 신청 인원으로 설정
                } else if(response.status === "unauthenticated") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/login";
                } else {
                    alert(response.message);
                    // 버튼 상태 복원
                    button.prop("disabled", false).text("신청");
                }
            },
            error: function(xhr, status, error){
                console.error("수강신청 AJAX 오류:", status, error);
                alert("수강신청 중 오류가 발생했습니다.");
                // 버튼 상태 복원
                button.prop("disabled", false).text("신청");
            }
        });
    });

    // 신청한 강의 삭제 버튼 클릭 시
    $(document).on("click", ".btn-remove-enrollment", function(){
        console.log("삭제 버튼 클릭됨");
        var enrollmentId = $(this).data("enrollment-id");
        var button = $(this);

        if(confirm("신청한 강의를 삭제하시겠습니까?")) {
            console.log("삭제 요청: enrollmentId=" + enrollmentId);

            $.ajax({
                url: "/enroll/remove",
                type: "POST",
                data: { enrollmentId: enrollmentId },
                success: function(response){
                    console.log("삭제 응답:", response);
                    if(response.status === "success") {
                        alert("신청한 강의가 삭제되었습니다.");
                        // 현재 학점 업데이트
                        if(response.currentCredits !== undefined) {
                            $("#currentCredits").text("현재 등록 학점: " + response.currentCredits + " 학점");
                        }
                        // 신청한 강의 목록에서 해당 행 제거
                        button.closest("tr").remove();
                        // 원래 수강신청 버튼 상태 복원 및 신청 인원 업데이트
                        $("#coursesTable button.btn-enroll[data-course-id='" + response.courseId + "']")
                            .removeClass("btn-disabled")
                            .prop("disabled", false)
                            .text("신청")
                            .closest("tr")
                            .find("td:nth-child(11)") // 신청 인원은 11번째 컬럼
                            .text(response.updatedEnrolled); // 서버에서 받은 업데이트된 신청 인원으로 설정
                    } else if(response.status === "unauthenticated") {
                        alert("로그인이 필요합니다.");
                        window.location.href = "/login";
                    } else {
                        alert(response.message);
                        // 버튼 상태 복원
                        button.prop("disabled", false).text("삭제");
                    }
                },
                error: function(xhr, status, error){
                    console.error("삭제 AJAX 오류:", status, error);
                    alert("삭제 중 오류가 발생했습니다.");
                    // 버튼 상태 복원
                    button.prop("disabled", false).text("삭제");
                }
            });
        }
    });
});
