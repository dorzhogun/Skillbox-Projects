package main;

import main.dto.DtoMessage;
import main.dto.DtoUser;
import main.dto.MessageMapper;
import main.dto.UserMapper;
import main.model.Message;
import main.model.User;
import main.model.MessageRepository;
import main.model.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChatController
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    private MessageMapper messageMapper;

    private UserMapper userMapper;

    public ChatController() {
    }

    @GetMapping("/init")
    public Map<String, Boolean> init()
    {
        Optional<User> userOpt = userRepository
                .findBySessionId(RequestContextHolder
                        .currentRequestAttributes()
                        .getSessionId());
        return Map.of("result", userOpt.isPresent());
    }

    @PostMapping("/auth")
    public Map<String, Boolean> auth(@RequestParam String name)
    {
        if(!Strings.isNotEmpty(name)) {
            return Map.of("result", false);
        }
        User user = new User();
        user.setName(name);
        user.setSessionId(RequestContextHolder.currentRequestAttributes().getSessionId());
        userRepository.save(user);
        return Map.of("result", true);
    }

    @PostMapping("/message")
    public Map<String, Boolean> sendMessage(@RequestParam String message)
    {
        if(!Strings.isNotEmpty(message)) {
            return Map.of("result", false);
        }
        Message msg = new Message();
        msg.setDateTime(LocalDateTime.now().withNano(0));
        msg.setMessage(message);
        msg.setUser(userRepository
                .findBySessionId(RequestContextHolder
                        .currentRequestAttributes()
                        .getSessionId()).get());
        messageRepository.saveAndFlush(msg);
        return Map.of("result", true);
    }

    @GetMapping("/message")
    public List<DtoMessage> getMessages()
    {
        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC,"dateTime"))
                .stream()
                .map(MessageMapper::map)
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public List<DtoUser> getUsersList()
    {
        return userRepository
                .findAll(Sort.by(Sort.Direction.ASC,"name"))
                .stream()
                .map(UserMapper::list)
                .collect(Collectors.toList());
    }
}
