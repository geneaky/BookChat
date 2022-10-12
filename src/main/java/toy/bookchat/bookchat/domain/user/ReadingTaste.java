package toy.bookchat.bookchat.domain.user;

public enum ReadingTaste {
    ECONOMY("경제"), PHILOSOPHY("철학"), HISTORY("역사"), TRAVEL("여행"), HEALTH("건상"), HOBBY("취미"),
    HUMANITIES("인문"), NOVEL("소설"), ART("예술"), DESIGN("디자인"), DEVELOPMENT("개발"), SCIENCE("과학"),
    MAGAZINE("잡지"), RELIGION("종교"), CHARACTER("인물");

    final private String readingTaste;

    private ReadingTaste(String readingTatse) {
        this.readingTaste = readingTatse;
    }

    public String getReadingTaste() {
        return readingTaste;
    }
}
