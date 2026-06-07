package kr.ac.hansung.service;

import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 💡전체 목록 페이징 (Pageable -> findAll에 그대로 전달)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // 💡키워드 검색 + 페이징
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContaining(keyword, pageable);
    }

    @Transactional
    public Product save(ProductDto dto) {
        Product product = new Product(
                dto.getName(), dto.getPrice(), dto.getDescription(), dto.getStock()
        );
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다: " + id));
    }

    // 💡상품 정보 수정 비즈니스 로직 (더티 체킹 자동 UPDATE)
    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        // findById: DB에서 엔티티 조회 (없으면 예외)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));

        // 영속 상태(Managed) 엔티티 필드 변경
        // -> @Transactional 종료 시 더티 체킹(Dirty Checking)으로 자동 UPDATE
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }

        return product; // 더티 체킹으로 자동 저장
    }
}