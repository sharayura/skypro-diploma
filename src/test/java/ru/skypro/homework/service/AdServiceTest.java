package ru.skypro.homework.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;


import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAds;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.Image;

import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;

import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.impl.AdService;
import ru.skypro.homework.service.impl.CommentService;
import ru.skypro.homework.service.impl.UserService;


import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;
import static ru.skypro.homework.service.ServiceTestFabric.*;



@SpringBootTest
public class AdServiceTest {


    @Mock
    private AdRepository adRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private AdMapper adMapper;
    @Mock
    private CommentService commentService;
    private AdService adService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adService = new AdService(adRepository, imageRepository, userRepository, userService, adMapper, commentService);
    }

    @Test
    public void testRemoveAd() {

        Ad ad = new Ad();
        ad.setImage(new Image());

        when(adRepository.findById(TEST_ID)).thenReturn(Optional.of(ad));

        adService.removeAd(TEST_ID);

        verify(imageRepository).delete(ad.getImage());
        verify(commentService).deleteCommentsByAdId(TEST_ID);
        verify(adRepository).deleteById(TEST_ID);
    }

    @Test
    public void testUpdateAdImage() throws IOException {
        MultipartFile image = new MockMultipartFile(TEST_FILE_NAME, TEST_FILE_CONTENT);
        when(adRepository.findById(TEST_ID)).thenReturn(Optional.of(new Ad()));
        when(imageRepository.findById(TEST_ID)).thenReturn(Optional.of(new Image()));
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());
        adService.updateAdImage(TEST_ID, image);

        verify(imageRepository).save(any(Image.class));
    }

    @Test
    public void testGetAllAds() {
        List<Ad> mockAds = new ArrayList<>();
        Ad ad1 = new Ad();
        ad1.setId(TEST_ID);
        ad1.setTitle(TEST_TITLE);
        ad1.setDescription(TEST_DESCRIPTION);
        mockAds.add(ad1);

        Ad ad2 = new Ad();
        ad2.setId(TEST_ID);
        ad2.setTitle(TEST_TITLE);
        ad2.setDescription(TEST_DESCRIPTION);
        mockAds.add(ad2);

        List<AdsDto> expectedAdsDtoList = new ArrayList<>();
        AdsDto expectedAdDto1 = new AdsDto();
        expectedAdDto1.setTitle(ad1.getTitle());
        expectedAdsDtoList.add(expectedAdDto1);

        AdsDto expectedAdDto2 = new AdsDto();
        expectedAdDto2.setTitle(ad2.getTitle());
        expectedAdsDtoList.add(expectedAdDto2);

        ResponseWrapperAds expectedResponse = new ResponseWrapperAds();
        expectedResponse.setCount(mockAds.size());
        expectedResponse.setResults(expectedAdsDtoList);

        when(adRepository.findAll()).thenReturn(mockAds);
        when(adMapper.adListToAdsDtoList(mockAds)).thenReturn(expectedAdsDtoList);
        ResponseWrapperAds result = adService.getAllAds();

        assertNotNull(result);
        assertNotNull(result.getResults());
        assertEquals(mockAds.size(), result.getCount());
        assertEquals(expectedResponse, result);
    }

    @Test
    public void testAddAd() throws IOException {
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setTitle(TEST_TITLE);

        MockMultipartFile file = new MockMultipartFile("file", TEST_FILE_NAME, TEST_FILE_CONTENT_TYPE, TEST_FILE_CONTENT);

        Image savedImage = new Image();
        savedImage.setId(1);
        savedImage.setMediaType(file.getContentType());
        savedImage.setData(file.getBytes());

        User user = new User();
        AdsDto expectedAdsDto = new AdsDto();

        Ad ad = new Ad();
        ad.setId(1);
        ad.setTitle(createAdsDto.getTitle());

        when(adMapper.toAd(createAdsDto)).thenReturn(ad);
        when(imageRepository.save(savedImage)).thenReturn(savedImage);
        when(userRepository.findByUsername(userService.getCurrentUsername())).thenReturn(Optional.of(user));
        when(adRepository.save(ad)).thenReturn(ad);
        when(adMapper.toAdsDto(ad)).thenReturn(expectedAdsDto);

        AdsDto result = adService.addAd(createAdsDto, file);

        assertNotNull(result);
    }


    @Test
    public void testGetAdImageNotFound() {
        when(adRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        Image result = adService.getAdImage(TEST_ID);
        assertNull(result);
    }

    @Test
    public void testGetAdImage() {
        Integer adId = 1;
        Image expectedImage = new Image();
        Ad ad = new Ad();
        ad.setImage(expectedImage);
        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        Image result = adService.getAdImage(adId);
        Assertions.assertEquals(expectedImage, result);
    }


    @Test
    public void testGetAdsNotFound() {
        when(adRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        FullAdsDto result = adService.getAds(TEST_ID);
        assertNull(result);
    }

    @Test
    public void testGetAds() {

        Ad ad = new Ad();
        ad.setId(TEST_ID);
        ad.setTitle(TEST_TITLE);
        ad.setDescription(TEST_DESCRIPTION);

        FullAdsDto expectedFullAdsDto = new FullAdsDto();
        expectedFullAdsDto.setPk(ad.getId());
        expectedFullAdsDto.setTitle(ad.getTitle());
        expectedFullAdsDto.setDescription(ad.getDescription());

        when(adRepository.findById(TEST_ID)).thenReturn(Optional.of(ad));
        when(adMapper.toFullAdsDto(ad)).thenReturn(expectedFullAdsDto);

        FullAdsDto result = adService.getAds(TEST_ID);

        Assertions.assertEquals(expectedFullAdsDto.getPk(), result.getPk());
        Assertions.assertEquals(expectedFullAdsDto.getTitle(), result.getTitle());
        Assertions.assertEquals(expectedFullAdsDto.getDescription(), result.getDescription());
    }


    @Test
    public void testGetAdsMe() {


        User user = new User();
        user.setId(1);
        user.setUsername(TEST_USERNAME);

        Ad ad1 = new Ad();
        ad1.setId(1);
        ad1.setTitle(TEST_TITLE + " 1");

        Ad ad2 = new Ad();
        ad2.setId(2);
        ad2.setTitle(TEST_TITLE + " 2");

        List<Ad> mockAds = new ArrayList<>();
        mockAds.add(ad1);
        mockAds.add(ad2);

        List<AdsDto> expectedAdsDtoList = new ArrayList<>();
        AdsDto expectedAdDto1 = new AdsDto();
        expectedAdDto1.setTitle(ad1.getTitle());
        expectedAdsDtoList.add(expectedAdDto1);

        AdsDto expectedAdDto2 = new AdsDto();
        expectedAdDto2.setTitle(ad2.getTitle());
        expectedAdsDtoList.add(expectedAdDto2);

        ResponseWrapperAds expectedResponse = new ResponseWrapperAds();
        expectedResponse.setCount(mockAds.size());
        expectedResponse.setResults(expectedAdsDtoList);

        when(userService.getCurrentUsername()).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(adRepository.findAllByUserId(user.getId())).thenReturn(mockAds);
        when(adMapper.adListToAdsDtoList(mockAds)).thenReturn(expectedAdsDtoList);

        ResponseWrapperAds result = adService.getAdsMe();
        assertNotNull(result);
        assertEquals(mockAds.size(), result.getCount());
        assertEquals(expectedAdsDtoList, result.getResults());
    }

    @Test
    public void testUpdateDto() {

        Integer newPrice = 999;

        Ad existingAd = new Ad();
        existingAd.setId(TEST_ID);
        existingAd.setTitle("Old Title");
        existingAd.setDescription("Old Description");
        existingAd.setPrice(499);

        CreateAdsDto updateProperties = new CreateAdsDto();
        updateProperties.setTitle(TEST_TITLE);
        updateProperties.setDescription(TEST_DESCRIPTION);
        updateProperties.setPrice(newPrice);

        AdsDto expectedAdsDto = new AdsDto();
        expectedAdsDto.setPk(TEST_ID);
        expectedAdsDto.setTitle(TEST_TITLE);
        expectedAdsDto.setPrice(newPrice);

        when(adRepository.findById(TEST_ID)).thenReturn(Optional.of(existingAd));
        when(adMapper.toAdsDto(existingAd)).thenReturn(expectedAdsDto);

        AdsDto result = adService.updateDto(TEST_ID, updateProperties);
        assertNotNull(result);
        assertEquals(TEST_ID, result.getPk());
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(newPrice, result.getPrice());
    }

    @Test
    public void testHasAdAccessUsernameTrue() {
        Ad ad = new Ad();
        User adCreator = new User();
        adCreator.setUsername(TEST_CREATOR_USERNAME);
        ad.setUser(adCreator);

        when(adRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(ad));
        when(userService.getCurrentUserRole()).thenReturn(TEST_USER_ROLE);
        when(userService.getCurrentUsername()).thenReturn(TEST_CREATOR_USERNAME);
        boolean result = adService.hasAdAccess(TEST_ID);
        assertTrue(result);
    }

    @Test
    public void testHasAdAccessUsernameFalse() {
        Ad ad = new Ad();
        User adCreator = new User();
        adCreator.setUsername(TEST_CREATOR_USERNAME);
        ad.setUser(adCreator);

        when(adRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(ad));
        when(userService.getCurrentUserRole()).thenReturn(TEST_USER_ROLE);
        when(userService.getCurrentUsername()).thenReturn(TEST_CURRENT_USERNAME);
        boolean result = adService.hasAdAccess(TEST_ID);
        assertFalse(result);
    }

    @Test
    public void testHasAdAccessAdmin() {
        Ad ad = new Ad();
        User adCreator = new User();
        adCreator.setUsername(TEST_CREATOR_USERNAME);
        ad.setUser(adCreator);

        when(adRepository.findById(TEST_ID)).thenReturn(java.util.Optional.of(ad));
        when(userService.getCurrentUserRole()).thenReturn(TEST_ADMIN_ROLE);
        when(userService.getCurrentUsername()).thenReturn(TEST_CURRENT_USERNAME);
        boolean result = adService.hasAdAccess(TEST_ID);
        assertTrue(result);
    }
}
