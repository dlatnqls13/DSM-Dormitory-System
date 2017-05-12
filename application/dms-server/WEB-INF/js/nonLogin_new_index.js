/***
 * background
 */
var $backgroundImage = $("#backgroundWallpaper");

/**
 * Panel
 */
var $panel = $("#panel");

/**
 * Common window
 */
var $windowClose = $(".window-close");

/**
 * Common modal
 */
var $modalButton = $(".modal-button");
var $closeModal = $(".btn-close");

/**
 * Menu
 */
var $menu = $("#menu");
var $menu2 = $("#menu2");
var $page1 = $("#page1");
var $page2 = $("#page2");
var $menuPagenation = $("#menu-pagenation");

/**
 * Extension
 */
var $openExtensionButton = $("#open-extension-apply");
var $closeExtensionButton = $("#close-extension-window");
var $extensionWindow = $("#extension-apply-window");
var $gaon = $("#extension-gaon");
var $naon = $("#extension-naon");
var $daon = $("#extension-daon");
var $laon = $("#extension-laon");
var $three = $("#extension-three");
var $four = $("#extension-four");
var $five = $("#extension-five");
var selectedClass = $("#extension-gaon");
var $classSelect = $(".extension-class-select");

/**
 * Going out
 */
var $openGoingOutButton = $(".goingOut-btn");
var $goingOutWindow = $("#going-out-apply-window");
var $closeGoingOutButton = $("#close-going-out-window");
var $goingOutApplyButton = $("#going-out-apply-btn");
var $goingOutPaperplane = $("#going-out-apply-btn i");
var $saturdayContainer = $(".saturday-container");
var $sundayContainer = $(".sunday-container");

/**
 * My page
 */
var $openMyPageButton = $(".mypage-btn");
var $mypageWindow = $(".mypage-window");
var $closeMypageWindow = $("#close-mypage-window");
var $passwordChangeBtn = $(".edit-password-container");
var $passwordChangeReq = $(".password-change button");

/**
 * Stay
 */
var $openStayButton = $("#open-stay-apply")
var $stayWindow = $(".stay-window");
var $stayApplyButton = $("#stay-apply-btn");
var $stayPaperplane = $("#stay-apply-btn i");
var $closeStayButton = $("#close-stay-window");
var stayDate = new Date();
/**
 * Meal
 */
var mealDate = new Date();
var $prevMenuBtn = $("#previous-menu");
var $nextMenuBtn = $("#next-menu");

/**
 * Domitory rule
 */
var $dormRule = $(".dorm-rule");
var $closeDormRuleButton = $("#close-rule-window");
var $dormListWindow = $(".rule-window");

/**
 * Domitory faq
 */
var $faqBtn = $(".faq-btn");
var $closeFaqButton = $("#faq-going-out-window");
var $faqListWindow = $(".faq-window");

/**
 * Facility
 */
var $facilityBtn = $(".facility-btn");
var $FacilityModal = $(".facility-modal-wrapper");

/**
 * bug
 */
var $bugBtn = $(".bug-btn");

/**
 * Login
 */
var $openLoginButton = $(".login-btn");
var $loginSendBtn = $(".login-button");

/**
 * Point
 */
var $openPointButton = $(".point-btn");

/**
 * Notice
 */
var $noticeMoreBtn = $(".notice-more");
var $closeNoticeButton = $("#close-notice-window");
var $noticeListWindow = $(".notice-window");

/**
 * Current state(stay)
 */
var $stayCurrentState = $('#Layer_1');

/**
 * Current state(extension)
 */
var $extensionCurrentState = $('#Layer_2');

/** ======================================================================================
 * Common window
========================================================================================== */
// $windowClose.on("click", function() {
//     $(this).parents(".window").toggleClass("fade-in");
//     $panel.toggleClass("left-move");
//     $menu.toggleClass("fade-out");
//     $menuPagenation.toggleClass("fade-out");
// });

/** ======================================================================================
 * Common modal
========================================================================================== */
$closeModal.on("click", function() {
    $(this).parents().parents().parents().parents(".modal-wrapper").toggleClass('open');
    // $panel.toggleClass('blur');
    // $menu.toggleClass('blur');
    return false;
});

function showLoginModal() {
    $('.login-modal-wrapper').toggleClass('open');
    return false;
}

/** ======================================================================================
 * Extension
========================================================================================== */

$openExtensionButton.on("click", function() {
    showLoginModal();
});

/** ======================================================================================
 * Notice
========================================================================================== */
$noticeMoreBtn.on("click", function() {
    showLoginModal();
});

function getNoticeList() {
    $.ajax({
        url: "http://dsm2015.cafe24.com/post/notice/list",
        type: "GET",
        success: function(data) {
            var parsedData = JSON.parse(data).result;
            parsedData.forEach(function(data) {
                fillListCard(data, $(".notice-window .list-box-container"));
            });
        },
        error: function() {
            console.log("error");
        }
    });
}
setNoticePreview();
getNoticeList();

function fillListCard(data, target) {
    var newCard = $('<div/>', {
        "class": "list-box",
    });
    newCard.append($('<p/>', {
        "class": "list-box-no",
        text: data.no
    }));
    newCard.append($('<p/>', {
        "class": "list-box-no-title",
        text: data.title
    }));
    // newCard.append($('<p/>', {
    //     "class": "list-box-writer",
    //     text: "사감부"
    // }));

    target.append(newCard);
}

function setNoticePreview() {
    $.ajax({
        url: "http://dsm2015.cafe24.com/post/notice/list",
        type: "GET",
        data: {
            page: 1,
            limit: 1
        },
        success: function(data) {
            var parsedData = JSON.parse(data).result;
            $("#notice-title").text(parsedData[0].title);
            $(".notice-content-container p").html(parsedData[0].content);
        },
        error: function() {
            console.log("error");
        }
    });
}


/** ======================================================================================
 * Dormitory rule
========================================================================================== */

/** ======================================================================================
 * My page
========================================================================================== */
$openMyPageButton.on("click", function() {
    showLoginModal();
});

$dormRule.on("click", function() {
    showLoginModal();
});

/** ======================================================================================
 * faq rule
========================================================================================== */

$faqBtn.on("click", function() {
    showLoginModal();
});

/** ======================================================================================
 * Stay
========================================================================================== */

$openStayButton.click(function() {
    showLoginModal();
});


/** ======================================================================================
 * Login
========================================================================================== */
$openLoginButton.on("click", function() {
    $('.login-modal-wrapper').toggleClass('open');
    // $panel.toggleClass('blur');
    // $menu.toggleClass('blur');
    return false;
});

$loginSendBtn.on("click", function() {
    $.ajax({
        url: "/account/login/student",
        type: "POST",
        data: {
            id: $(".login-input #name").val(),
            password: $(".login-input #pass").val(),
            remember: $(".login-check input:checked").val(),
            "g-recaptcha-response": grecaptcha.getResponse()
        },
        success: function(data, status) {
            location.reload();
        },
        error: function(xhr) {
            alert("로그인에 실패했습니다.");
        },
    });
});

/** ======================================================================================
 * Bug modal
========================================================================================== */
$bugBtn.on("click", function() {
    $('.bug-modal-wrapper').toggleClass('open');
    // $panel.toggleClass('blur');
    // $menu.toggleClass('blur');
    return false;
});

$(".report-bug").on("click", function() {
    $.ajax({
        url: "/post/bug",
        type: "POST",
        data: {
            title: $("#bug-title").val(),
            content: $("#bug-content").val()
        },
        success: function() {
            alert("버그를 제보해 주셔서 고맙습니다!");
            $("#bugModal button:nth-child(2)").click();
        },
        error: function() {
            alert("버그신고에 실패했어요 TT");
        }
    });
});

/** ======================================================================================
 * Facility modal
========================================================================================== */
$facilityBtn.on("click", function() {
    showLoginModal();
});

/** ======================================================================================
 * Point
 ========================================================================================== */
$openPointButton.on("click", function() {

});

/** ======================================================================================

 * Going out
========================================================================================== */
$openGoingOutButton.on("click", function() {
    showLoginModal();
});

/** ======================================================================================
 * Current state (stay)
========================================================================================== */

/** ======================================================================================
 * Current state (extension)
========================================================================================== */

/** ======================================================================================
 * menu
========================================================================================== */
$page1.click(function() {
    if ($page1.hasClass("current-index")) {} else {
        $page2.removeClass("current-index");
        $page1.addClass("current-index");

        $menu2.removeClass("show-page");
        $menu2.addClass("hide-page");

        $menu.removeClass("hide-page");
        $menu.addClass("show-page");
    }
});

$page2.click(function() {
    if ($page2.hasClass("current-index")) {} else {
        $page1.removeClass("current-index");
        $page2.addClass("current-index");

        $menu.removeClass("show-page");
        $menu.addClass("hide-page");

        $menu2.removeClass("hide-page");
        $menu2.addClass("show-page");
    }
});

/** ======================================================================================
 * modal
========================================================================================== */
$modalButton.click(function(e) {
    var pX = e.pageX,
        pY = e.pageY,
        oX = parseInt($(this).offset().left),
        oY = parseInt($(this).offset().top);

    $(this).append('<span class="click-efect x-' + oX + ' y-' + oY + '" style="margin-left:' + (pX - oX) + 'px;margin-top:' + (pY - oY) + 'px;"></span>')
    $('.x-' + oX + '.y-' + oY + '').animate({
        "width": "500px",
        "height": "500px",
        "top": "-250px",
        "left": "-250px",
    }, 200);
    $('.x-' + oX + '.y-' + oY + '').animate({
        "width": "0",
        "height": "0",
        "top": "-0",
        "left": "-0",
    }, 600, function() {
        $(".click-efect").remove();
        console.log("remove")
    });
    $("button", this).addClass('active');
});

(function() {
    $(".login-input input").focus(function() {
        $(this).parent(".login-input").each(function() {
            $("label", this).css({
                "line-height": "18px",
                "font-size": "18px",
                "font-weight": "100",
                "top": "0px"
            })
            $(".spin", this).css({
                "width": "100%"
            })
        });
    }).blur(function() {
        $(".spin").css({
            "width": "0px"
        })
        if ($(this).val() == "") {
            $(this).parent(".login-input").each(function() {
                $("label", this).css({
                    "line-height": "60px",
                    "font-size": "24px",
                    "font-weight": "300",
                    "top": "10px"
                })
            });

        }
    });

    $(".bug-content textarea").focus(function() {
        $(this).parent(".bug-content").each(function() {
            $("label", this).css({
                "line-height": "18px",
                "font-size": "18px",
                "font-weight": "100",
                "top": "0px"
            })
            $(".spin", this).css({
                "width": "100%"
            })
        });
    }).blur(function() {
        $(".spin").css({
            "width": "0px"
        })
        if ($(this).val() == "") {
            $(this).parent(".login-input").each(function() {
                $("label", this).css({
                    "line-height": "60px",
                    "font-size": "24px",
                    "font-weight": "300",
                    "top": "10px"
                })
            });

        }
    });
})();
/** ======================================================================================
 * meal
========================================================================================== */
$prevMenuBtn.on("click", function() {
    prevDay();
    setDay();
});

$nextMenuBtn.on("click", function() {
    nextDay();
    setDay();
});

function nextDay() {
    mealDate.setDate(mealDate.getDate() + 1);
    getMeal();
}

function prevDay() {
    mealDate.setDate(mealDate.getDate() - 1);
    getMeal();
}

function formatDate() {
    return mealDate.toISOString().slice(0, 10);
}

function formatDate2() {
    var days = ["일", "월", "화", "수", "목", "금", "토"];
    var y = mealDate.getFullYear();
    var m = mealDate.getMonth() + 1;
    var d = mealDate.getDate();
    var day = days[mealDate.getDay()];

    return y + "." + m + "." + d + " " + day + "요일";
}

function setDay() {
    $(".meal-date").text(formatDate2());
    getMeal();
}

function getMeal() {
    $.ajax({
        url: "http://dsm2015.cafe24.com/meal",
        data: {
            date: formatDate()
        },
        statusCode: {
            200: function(data) {
                var parsedData = JSON.parse(data);
                var domArr = $(".meal-content p");
                $(domArr[0]).text(JSON.parse(parsedData.breakfast).toString());
                $(domArr[1]).text(JSON.parse(parsedData.lunch).toString());
                $(domArr[2]).text(JSON.parse(parsedData.dinner).toString());
            },
            error: function() {
                var domArr = $(".meal-content p");
                $(domArr[0]).text("급식이 없습니다.");
                $(domArr[1]).text("급식이 없습니다.");
                $(domArr[2]).text("급식이 없습니다.");
            }
        }
    })
}

//Sets the document when it is loaded
$(document).ready(function() {
    //set random background image
    //$backgroundImage.attr("src", ".\\images\\wallpaper" + (Math.floor(Math.random() * 9) + 1) + ".jpg");

    var agent = navigator.userAgent.toLowerCase();

    // if (agent.indexOf("chrome") != -1) {
    //     alert("크롬 브라우저입니다.");
    // }
    // if (agent.indexOf("safari") != -1) {
    //     alert("사파리 브라우저입니다.");
    // }
    // if (agent.indexOf("firefox") != -1) {
    //     alert("파이어폭스 브라우저입니다.");
    // }

    //show current stay state and extension state
    stayTick = $('#stayTick');
    stayCircle = $('#stayCheckCircle');
    stayCross1 = $('#stayCross1');
    stayCross2 = $('#stayCross2');
    stayDoCheck();

    extensionTick = $('#extensionTick');
    extensionCircle = $('#extensionCheckcircle');
    extensionCross1 = $('#extensionCross1');
    extensionCross2 = $('#extensionCross2');
    extensionDoCheck();


    //saturday, sunday svg animations
    var ids = ["#letter-s", "#letter-a", "#letter-t", "#letter-t2", "#letter-s2", "#letter-u", "#letter-n"];
    var path = $("path");

    $saturdayContainer.hover(function() {
            path[0].style.strokeDasharray = path[0].getTotalLength();
            path[0].style.strokeDashoffset = path[0].getTotalLength();
            $(ids[0]).animate({
                strokeDashoffset: '0'
            }, 600);

            path[1].style.strokeDasharray = path[1].getTotalLength();
            path[1].style.strokeDashoffset = path[1].getTotalLength();
            $(ids[1]).animate({
                strokeDashoffset: '0'
            }, 600);

            path[2].style.strokeDasharray = path[2].getTotalLength();
            path[2].style.strokeDashoffset = path[2].getTotalLength();
            $(ids[2]).animate({
                strokeDashoffset: '0'
            }, 600);

            path[3].style.strokeDasharray = path[3].getTotalLength();
            path[3].style.strokeDashoffset = path[3].getTotalLength();
            $(ids[3]).animate({
                strokeDashoffset: '0'
            }, 600);
        },
        function() {

        });

    $sundayContainer.hover(function() {
            path[4].style.strokeDasharray = path[4].getTotalLength();
            path[4].style.strokeDashoffset = path[4].getTotalLength();
            $(ids[4]).animate({
                strokeDashoffset: '0'
            }, 600);

            path[5].style.strokeDasharray = path[5].getTotalLength();
            path[5].style.strokeDashoffset = path[5].getTotalLength();
            $(ids[5]).animate({
                strokeDashoffset: '0'
            }, 600);

            path[6].style.strokeDasharray = path[6].getTotalLength();
            path[6].style.strokeDashoffset = path[6].getTotalLength();
            $(ids[6]).animate({
                strokeDashoffset: '0'
            }, 600);
        },
        function() {

        });

    //setting for show meal
    setDay();
});