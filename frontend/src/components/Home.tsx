import { useEffect, useState } from 'react';
import axios, { AxiosError } from 'axios';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import type { NewsItem } from '../types/NewsItem';

const HomeContainer = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: white;
  padding: 20px;
`;

const SearchContainer = styled.form`
  display: flex;
  justify-content: center;
  padding: 20px;
  background-color: #ff0000;
  margin-bottom: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const SearchInput = styled.input`
  padding: 12px 20px;
  width: 60%;
  max-width: 600px;
  border: 2px solid black;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s ease;

  &:focus {
    box-shadow: 0 0 0 3px rgba(255, 0, 0, 0.3);
  }
`;

const NewsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 25px;
  padding: 0 20px;
  overflow-y: auto;

  @media (max-width: 768px) {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
`;

const NewsCard = styled.div`
  background-color: white;
  border: 2px solid black;
  border-radius: 10px;
  padding: 15px;
  cursor: pointer;
  min-height: 250px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  }
`;

const NewsImage = styled.img`
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
`;

const NewsTitle = styled.h3`
  color: black;
  font-size: 1.1rem;
  margin-bottom: 10px;
  flex-grow: 1;
  line-height: 1.4;
`;

const NewsDescription = styled.p`
  color: #444;
  font-size: 0.9rem;
  margin-bottom: 10px;
  line-height: 1.4;
`;

const ReadMore = styled.button`
  background-color: #ff0000;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  align-self: flex-start;
  transition: background-color 0.2s;
  font-weight: 500;

  &:hover {
    background-color: #cc0000;
  }
`;

const ErrorMessage = styled.div`
  color: #ff0000;
  font-size: 1.2rem;
  text-align: center;
  padding: 40px;
`;

const LoadingMessage = styled.div`
  color: #444;
  font-size: 1.2rem;
  text-align: center;
  padding: 40px;
`;

const Home = () => {
  const [news, setNews] = useState<NewsItem[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTrendingNews = async () => {
      try {
        const response = await axios.get<NewsItem[]>('/api/news/trending');
        setNews(response.data.slice(0, 9));
      } catch (err) {
        const error = err as AxiosError;
        setError(error.message || 'Failed to fetch trending news. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchTrendingNews();
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const handleNewsClick = (url: string) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  };

  if (loading) return <LoadingMessage>Loading trending tech news...</LoadingMessage>;
  if (error) return <ErrorMessage>{error}</ErrorMessage>;

  return (
    <HomeContainer>
      <SearchContainer onSubmit={handleSearch}>
        <SearchInput 
          type="text"
          placeholder="Search tech news..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          aria-label="Search tech news"
        />
      </SearchContainer>

      <NewsGrid>
        {news.map((item, index) => (
          <NewsCard key={index} onClick={() => handleNewsClick(item.link)}>
            {item.image_url && (
              <NewsImage 
                src={item.image_url} 
                alt={item.title}
                onError={(e) => {
                  (e.target as HTMLImageElement).style.display = 'none';
                }}
              />
            )}
            <NewsTitle>{item.title}</NewsTitle>
            {item.description && (
              <NewsDescription>{item.description}</NewsDescription>
            )}
            <ReadMore aria-label={`Read more about ${item.title}`}>
              Read More
            </ReadMore>
          </NewsCard>
        ))}
      </NewsGrid>
    </HomeContainer>
  );
};

export default Home;