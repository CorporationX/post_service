package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private final AlbumRepository albumRepository;
    private final UserServiceClient userServiceClient;

    public AlbumService(AlbumRepository albumRepository, UserServiceClient userServiceClient) {
        this.albumRepository = albumRepository;
        this.userServiceClient = userServiceClient;
    }


    public List<Album> getAlbumsForUser(Long userId) {
        List<Album> albums = albumRepository.findAll();
        return albums.stream()
                .filter(album -> canUserViewAlbum(userId, album))
                .collect(Collectors.toList());
    }

    private boolean canUserViewAlbum(Long userId, Album album) {
        switch (album.getVisibility()) {
            case PUBLIC:
                return true;
            case SUBSCRIBERS:
                List<Long> subscribers = userServiceClient.getSubscribers(album.getAuthorId());
                return subscribers.contains(userId);
            case SELECTED_USERS:
                List<Long> selectedUsers = userServiceClient.getSelectedUsers(album.getAuthorId());
                return selectedUsers.contains(userId);
            case AUTHOR:
                return album.getAuthorId().equals(userId);
            default:
                return false;
        }
    }
}