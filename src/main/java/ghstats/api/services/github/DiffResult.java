package ghstats.api.services.github;

import java.util.List;

record DiffResult(List<String> added, List<String> removed, List<String> modified) {
}
