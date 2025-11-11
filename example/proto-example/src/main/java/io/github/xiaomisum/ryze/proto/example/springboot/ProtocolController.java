package io.github.xiaomisum.ryze.proto.example.springboot;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;
import xyz.migoo.framework.common.pojo.Result;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class ProtocolController {

    private static final Map<Integer, UserOuterClass.User> users = Maps.newHashMap();

    @GetMapping
    public Result<?> getUsers() {
        return Result.getSuccessful(users.values());
    }

    @GetMapping(value = "/{id}")
    public UserOuterClass.User getUser(@PathVariable("id") Integer id) {
        return users.get(id);
    }

    @PostMapping(produces = "application/x-protobuf")
    public UserOuterClass.User addUser(@RequestBody UserOuterClass.User user) {
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(produces = "application/x-protobuf")
    public UserOuterClass.User updateUser(@RequestBody UserOuterClass.User user) {
        users.put(user.getId(), user);
        return user;
    }


    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            users.put(i, UserOuterClass.User.newBuilder().setId(i).setName("name_" + i).setAge(18).build());
        }
    }
}