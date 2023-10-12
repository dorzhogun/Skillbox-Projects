$(function ()
{
    let getUserElement = function (user)
    {
        let userItem = $('<div class="user-item"></div>');
        userItem.append($('<div class="user-item">' + user.name + '<div>'));
        return userItem;

    };
    let getMessageElement = function (message)
    {
        let item = $('<div class="message-item"></div>');
        let header = $('<div class="message-header"></div>');
        header.append($('<div class="datetime">' +
            message.datetime + '</div>'));
        header.append($('<div class="username">' +
            message.username + '</div>'));
        let textElement = $('<div class="message-text"></div>');
        textElement.text(message.text);
        item.append(header, textElement);
        return item;
    };

    let updateMessages = function ()
    {
        $('.messages-list').html('');
        $.get('/message', {}, function (response) {
            if(response.length == 0) {
                return;
            }
            $(`.messages-list`).html('');
            for(i in response)
            {
                let element = getMessageElement(response[i]);
                $('.messages-list').append(element);
            }
        });
    };

    let updateUsersList = function()
    {
        let list = $('.users-list');
        list.html('');
        $.get('/user', {}, function (response)
        {
            if(response.length == 0) {
                return;
            }
            list.html('');
            for(i in response)
            {
                let userElement = getUserElement(response[i]);
                $('.users-list').append(userElement);
            }
        });
    };
    let initApplication = function ()
    {
        $('.messages-and-users').css({display: 'flex'});
        $('.controls').css({display: 'flex'});

        $('.send-message').on('click', function ()
        {
            let message = $('.new-message').val();
            $.post('/message', {message: message}, function (response) {
               if(response.result) {
                   $('.new-message').val('');
               } else {
                   alert('Ошибка :( Повторите попытку позже');
               }
           });
        });
        setInterval(updateMessages, 3000);
        setInterval(updateUsersList, 3000);
    };

    let registerUser = function (name)
    {
        $.post('/auth', {name: name}, function (response) {
            if(response.result) {
                initApplication();
            }
        });
    };


    $.get('/init', {}, function(response){
        if(!response.result)
        {
            let name = prompt('Введите Ваше имя:');
            registerUser(name);
            return;
        }
        initApplication();
    });
});