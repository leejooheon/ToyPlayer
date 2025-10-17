from pathlib import Path
from textwrap import dedent

OUTPUT_DIR = Path("docs/diagrams")
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

SVG_HEADER = """<svg xmlns='http://www.w3.org/2000/svg' width='{width}' height='{height}' viewBox='0 0 {width} {height}' font-family='Inter,Arial,sans-serif'>\n<style>.title{{font-size:22px;font-weight:bold;fill:#1f2933;}}.box{{fill:#eaf2fb;stroke:#1f4d7a;stroke-width:1.5;rx:12;ry:12;}}.label{{font-size:13px;fill:#102a43;text-anchor:middle;}}.caption{{font-size:12px;fill:#52606d;text-anchor:middle;}}.arrow{{stroke:#334e68;stroke-width:1.4;marker-end:url(#arrowhead);}}.arrow-text{{font-size:12px;fill:#334e68;text-anchor:middle;}}</style>\n<defs><marker id='arrowhead' markerWidth='10' markerHeight='7' refX='10' refY='3.5' orient='auto'><polygon points='0 0, 10 3.5, 0 7' fill='#334e68'/></marker></defs>\n"""

SVG_FOOTER = "</svg>\n"


def box(x, y, w, h, title, lines):
    text_lines = "".join(
        f"<tspan x='{x + w/2}' dy='{14 if i == 0 else 16}'>{line}</tspan>"
        for i, line in enumerate(lines)
    )
    return dedent(
        f"""
        <g>
            <rect class='box' x='{x}' y='{y}' width='{w}' height='{h}' />
            <text class='label' x='{x + w/2}' y='{y + 22}'>{title}</text>
            <text class='caption' x='{x + w/2}' y='{y + 40}'>
                {text_lines}
            </text>
        </g>
        """
    )


def arrow(x1, y1, x2, y2, text=""):
    midx, midy = (x1 + x2) / 2, (y1 + y2) / 2
    label = f"<text class='arrow-text' x='{midx}' y='{midy - 6}'>{text}</text>" if text else ""
    return dedent(
        f"""
        <g>
            <line class='arrow' x1='{x1}' y1='{y1}' x2='{x2}' y2='{y2}' />
            {label}
        </g>
        """
    )


def title(text, x, y):
    return f"<text class='title' x='{x}' y='{y}'>{text}</text>\n"


def write_svg(filename, width, height, body):
    content = SVG_HEADER.format(width=width, height=height) + body + SVG_FOOTER
    (OUTPUT_DIR / filename).write_text(content, encoding="utf-8")


def diagram_build_logic():
    width, height = 900, 520
    elements = [
        title("Module registration & dependency management", width/2 - 240, 40),
        box(40, 80, 220, 140, "settings.gradle.kts", ["includeBuild(\"build-logic\")", "include(:modules)"]),
        box(340, 80, 220, 140, "build-logic", ["toyplayer.* convention", "plugins (application/feature)"]),
        box(640, 80, 220, 140, "Module build.gradle.kts", ["apply(plugin ids)", "configure android/kotlin"]),
        arrow(260, 150, 340, 150, "exposes"),
        arrow(560, 150, 640, 150, "applies"),
        box(340, 260, 220, 140, "gradle/libs.versions.toml", ["centralized versions", "aliases -> libs.*"]),
        arrow(450, 220, 450, 260, "version catalog"),
        box(100, 410, 240, 120, "Project modules", ["app, features, core", "domain, data, testing"]),
        box(520, 410, 240, 120, "Dependency blocks", ["implementation(libs.*)", "api(libs.*)"]),
        arrow(150, 220, 180, 410, "included modules"),
        arrow(760, 220, 700, 410, "consumes catalog"),
        arrow(450, 400, 220, 410, "type-safe access"),
        arrow(450, 400, 520, 410, "shared coordinates"),
    ]
    write_svg("01-build-logic.svg", width, height, "".join(elements))


def diagram_media3_flow():
    width, height = 900, 540
    elements = [
        title("Media3 playback collaboration", width/2 - 200, 40),
        box(60, 80, 240, 150, "MusicService", ["MediaLibraryService", "builds MediaSession", "injects ToyPlayer"]),
        box(520, 80, 240, 150, "MusicStateHolder", ["shared flows", "media items", "playback flags"]),
        arrow(300, 150, 520, 150, "player callbacks"),
        arrow(520, 210, 300, 210, "state updates"),
        box(60, 260, 240, 140, "Playback use cases", ["PlaybackUseCase", "PlaybackErrorUseCase", "PlaybackLogUseCase"]),
        arrow(180, 230, 180, 260, "collects & reacts"),
        box(60, 420, 240, 110, "ToyPlayer", ["Media3 player", "lifecycle managed by service"]),
        arrow(180, 400, 180, 420, "controls"),
        box(520, 260, 240, 140, "MediaLibrarySessionCallback", ["custom layout", "command routing"]),
        arrow(300, 240, 520, 300, "invalidate layout"),
        box(320, 420, 240, 110, "PlayerController", ["MediaBrowser client", "enqueue / seek / commands"]),
        arrow(320, 460, 180, 460, "controls via session"),
        arrow(560, 420, 640, 330, "observes state flows"),
    ]
    write_svg("02-media3-flow.svg", width, height, "".join(elements))


def diagram_data_contract():
    width, height = 900, 540
    elements = [
        title("Domain ↔ Data playlist contract", width/2 - 210, 40),
        box(40, 80, 240, 140, "domain/repository-api", ["PlaylistRepository", "Result & Flow contract"]),
        box(340, 80, 240, 140, "domain/usecase", ["PlaylistUseCase", "DefaultSettingsUseCase"]),
        box(640, 80, 220, 140, "features", ["PlayerViewModel", "Screen controllers"]),
        arrow(280, 150, 340, 150, "depends on"),
        arrow(580, 150, 640, 150, "injects"),
        box(40, 260, 240, 150, "data/repository", ["RepositoryModule", "PlaylistRepositoryImpl"]),
        arrow(160, 220, 160, 260, "Hilt binding"),
        box(340, 260, 240, 150, "data/playlist", ["PlaylistDataSource", "maps DAO -> domain"]),
        arrow(280, 330, 340, 330, "constructor"),
        box(640, 260, 220, 150, "data/playlist/dao", ["PlaylistDao", "entities & queries"]),
        arrow(580, 330, 640, 330, "Room mapping"),
        box(340, 430, 240, 100, "Extending data", ["1. Update DAO/source", "2. Adapt DataSource", "3. Adjust RepositoryImpl", "4. Expose via interface"]),
        arrow(460, 410, 460, 430, "steps"),
    ]
    write_svg("03-data-contract.svg", width, height, "".join(elements))


def diagram_player_ui():
    width, height = 900, 540
    elements = [
        title("Compose UI & ViewModel state loop", width/2 - 220, 40),
        box(40, 80, 260, 150, "PlayerViewModel", ["combine music state + playlists", "StateFlow uiState", "dispatch(PlayerEvent)"]),
        box(540, 80, 260, 150, "PlayerScreen", ["collectAsStateWithLifecycle", "gestures -> PlayerEvent", "delegate navigation"]),
        arrow(300, 150, 540, 150, "uiState flow"),
        box(40, 260, 260, 140, "MusicStateHolder", ["playback/duration flows", "error events"]),
        arrow(170, 230, 170, 260, "collect"),
        box(540, 260, 260, 140, "UI components", ["InsidePager", "InfoSection", "LogoSection"]),
        arrow(670, 230, 670, 260, "render state"),
        box(40, 420, 260, 110, "Use cases", ["PlaylistUseCase", "DefaultSettingsUseCase", "VisualizerObserver"]),
        arrow(170, 400, 170, 420, "business data"),
        box(540, 420, 260, 110, "PlayerController", ["MediaBrowser commands", "play/pause/seek"]),
        arrow(540, 430, 300, 170, "dispatch events"),
        arrow(670, 420, 670, 230, "event -> action"),
    ]
    write_svg("04-player-ui.svg", width, height, "".join(elements))


def diagram_testing():
    width, height = 780, 440
    elements = [
        title("Testing utilities & shared dependencies", width/2 - 230, 40),
        box(40, 80, 220, 130, "testing module", ["toyplayer.android.library", "namespace com.jooheon.testing"]),
        box(300, 80, 220, 130, "build.gradle.kts", ["api junit4 + vintage", "mockk, turbine", "kotlinx-coroutines-test"]),
        box(560, 80, 180, 130, "Consumers", ["feature/domain tests", "import shared deps"]),
        arrow(260, 150, 300, 150, "shares"),
        arrow(520, 150, 560, 150, "exposes"),
        box(220, 260, 240, 130, "MainDispatcherRule", ["set Dispatchers.Main", "reset after test"]),
        arrow(340, 210, 340, 260, "@get:Rule"),
        box(500, 260, 220, 130, "Coroutine/Compose tests", ["use rule for stable dispatcher", "combine with Turbine, runTest"]),
        arrow(440, 300, 500, 300, "reliable scheduling"),
    ]
    write_svg("05-testing.svg", width, height, "".join(elements))


def main():
    diagram_build_logic()
    diagram_media3_flow()
    diagram_data_contract()
    diagram_player_ui()
    diagram_testing()


if __name__ == "__main__":
    main()
