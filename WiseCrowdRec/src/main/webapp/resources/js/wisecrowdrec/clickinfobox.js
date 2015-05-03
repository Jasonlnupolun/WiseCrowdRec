$(function () {
    $('body').on('click', '.btn-float', function (event) {
        event.preventDefault();
        var ele = $(this);
        $(this).parent().addClass('open');
        $(this).addClass('slide');
        setTimeout(function () {
            ele.parent().siblings().fadeIn();
            setTimeout(function () {
                ele.parent().removeClass('open');
                ele.removeClass('slide');
            }, 1);
        }, 1);
    });
    $('body').on('click', '.close', function (event) {
        event.preventDefault();
        $(this).parent().fadeOut();
    });
});