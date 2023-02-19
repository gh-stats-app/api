package ghstats.api.integrations.github;

import ghstats.api.BaseIntegrationTest;
import ghstats.api.actions.api.ActionId;
import ghstats.api.services.slack.SlackClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.Duration;

class GithubEventsApiTest extends BaseIntegrationTest {

    @MockBean
    SlackClient slackClient;

    @Test
    @DisplayName("should parse github event")
    void testGithubEventParsing() {
        // given
        Mockito.when(slackClient.sendUnlockedMessage(Mockito.any())).thenReturn(Mono.empty());

        // expect
        webClient.post()
                .uri("/integrations/github/events")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(exampleGithubEvent))
                .exchange()
                .expectStatus()
                .isAccepted();
    }

    String exampleGithubEvent = """
            {
                 "ref": "refs/heads/main",
                 "before": "fb22d6ace1fab28882f6643dfbfb99a26d20fa00",
                 "after": "bb9203e5ea45d7013c68a3c65da027be5428bfea",
                 "repository": {
                   "id": 398612653,
                   "node_id": "MDEwOlJlcG9zaXRvcnkzOTg2MTI2NTM=",
                   "name": "gh-events-test",
                   "full_name": "bgalek/gh-events-test",
                   "private": false,
                   "owner": {
                     "name": "bgalek",
                     "email": "bartosz@email.local",
                     "login": "bgalek",
                     "id": 2291045,
                     "node_id": "MDQ6VXNlcjIyOTEwNDU=",
                     "avatar_url": "https://avatars.githubusercontent.com/u/2291045?v=4",
                     "gravatar_id": "",
                     "url": "https://api.github.com/users/bgalek",
                     "html_url": "https://github.com/bgalek",
                     "followers_url": "https://api.github.com/users/bgalek/followers",
                     "following_url": "https://api.github.com/users/bgalek/following{/other_user}",
                     "gists_url": "https://api.github.com/users/bgalek/gists{/gist_id}",
                     "starred_url": "https://api.github.com/users/bgalek/starred{/owner}{/repo}",
                     "subscriptions_url": "https://api.github.com/users/bgalek/subscriptions",
                     "organizations_url": "https://api.github.com/users/bgalek/orgs",
                     "repos_url": "https://api.github.com/users/bgalek/repos",
                     "events_url": "https://api.github.com/users/bgalek/events{/privacy}",
                     "received_events_url": "https://api.github.com/users/bgalek/received_events",
                     "type": "User",
                     "site_admin": false
                   },
                   "html_url": "https://github.com/bgalek/gh-events-test",
                   "description": null,
                   "fork": false,
                   "url": "https://github.com/bgalek/gh-events-test",
                   "forks_url": "https://api.github.com/repos/bgalek/gh-events-test/forks",
                   "keys_url": "https://api.github.com/repos/bgalek/gh-events-test/keys{/key_id}",
                   "collaborators_url": "https://api.github.com/repos/bgalek/gh-events-test/collaborators{/collaborator}",
                   "teams_url": "https://api.github.com/repos/bgalek/gh-events-test/teams",
                   "hooks_url": "https://api.github.com/repos/bgalek/gh-events-test/hooks",
                   "issue_events_url": "https://api.github.com/repos/bgalek/gh-events-test/issues/events{/number}",
                   "events_url": "https://api.github.com/repos/bgalek/gh-events-test/events",
                   "assignees_url": "https://api.github.com/repos/bgalek/gh-events-test/assignees{/user}",
                   "branches_url": "https://api.github.com/repos/bgalek/gh-events-test/branches{/branch}",
                   "tags_url": "https://api.github.com/repos/bgalek/gh-events-test/tags",
                   "blobs_url": "https://api.github.com/repos/bgalek/gh-events-test/git/blobs{/sha}",
                   "git_tags_url": "https://api.github.com/repos/bgalek/gh-events-test/git/tags{/sha}",
                   "git_refs_url": "https://api.github.com/repos/bgalek/gh-events-test/git/refs{/sha}",
                   "trees_url": "https://api.github.com/repos/bgalek/gh-events-test/git/trees{/sha}",
                   "statuses_url": "https://api.github.com/repos/bgalek/gh-events-test/statuses/{sha}",
                   "languages_url": "https://api.github.com/repos/bgalek/gh-events-test/languages",
                   "stargazers_url": "https://api.github.com/repos/bgalek/gh-events-test/stargazers",
                   "contributors_url": "https://api.github.com/repos/bgalek/gh-events-test/contributors",
                   "subscribers_url": "https://api.github.com/repos/bgalek/gh-events-test/subscribers",
                   "subscription_url": "https://api.github.com/repos/bgalek/gh-events-test/subscription",
                   "commits_url": "https://api.github.com/repos/bgalek/gh-events-test/commits{/sha}",
                   "git_commits_url": "https://api.github.com/repos/bgalek/gh-events-test/git/commits{/sha}",
                   "comments_url": "https://api.github.com/repos/bgalek/gh-events-test/comments{/number}",
                   "issue_comment_url": "https://api.github.com/repos/bgalek/gh-events-test/issues/comments{/number}",
                   "contents_url": "https://api.github.com/repos/bgalek/gh-events-test/contents/{+path}",
                   "compare_url": "https://api.github.com/repos/bgalek/gh-events-test/compare/{base}...{head}",
                   "merges_url": "https://api.github.com/repos/bgalek/gh-events-test/merges",
                   "archive_url": "https://api.github.com/repos/bgalek/gh-events-test/{archive_format}{/ref}",
                   "downloads_url": "https://api.github.com/repos/bgalek/gh-events-test/downloads",
                   "issues_url": "https://api.github.com/repos/bgalek/gh-events-test/issues{/number}",
                   "pulls_url": "https://api.github.com/repos/bgalek/gh-events-test/pulls{/number}",
                   "milestones_url": "https://api.github.com/repos/bgalek/gh-events-test/milestones{/number}",
                   "notifications_url": "https://api.github.com/repos/bgalek/gh-events-test/notifications{?since,all,participating}",
                   "labels_url": "https://api.github.com/repos/bgalek/gh-events-test/labels{/name}",
                   "releases_url": "https://api.github.com/repos/bgalek/gh-events-test/releases{/id}",
                   "deployments_url": "https://api.github.com/repos/bgalek/gh-events-test/deployments",
                   "created_at": 1629565272,
                   "updated_at": "2021-10-06T12:31:52Z",
                   "pushed_at": 1676640026,
                   "git_url": "git://github.com/bgalek/gh-events-test.git",
                   "ssh_url": "git@github.com:bgalek/gh-events-test.git",
                   "clone_url": "https://github.com/bgalek/gh-events-test.git",
                   "svn_url": "https://github.com/bgalek/gh-events-test",
                   "homepage": null,
                   "size": 8,
                   "stargazers_count": 0,
                   "watchers_count": 0,
                   "language": "JavaScript",
                   "has_issues": true,
                   "has_projects": true,
                   "has_downloads": true,
                   "has_wiki": true,
                   "has_pages": false,
                   "has_discussions": false,
                   "forks_count": 0,
                   "mirror_url": null,
                   "archived": false,
                   "disabled": false,
                   "open_issues_count": 0,
                   "license": null,
                   "allow_forking": true,
                   "is_template": false,
                   "web_commit_signoff_required": false,
                   "topics": [
               
                   ],
                   "visibility": "public",
                   "forks": 0,
                   "open_issues": 0,
                   "watchers": 0,
                   "default_branch": "main",
                   "stargazers": 0,
                   "master_branch": "main"
                 },
                 "pusher": {
                   "name": "bgalek",
                   "email": "bartosz@email.local"
                 },
                 "sender": {
                   "login": "bgalek",
                   "id": 2291045,
                   "node_id": "MDQ6VXNlcjIyOTEwNDU=",
                   "avatar_url": "https://avatars.githubusercontent.com/u/2291045?v=4",
                   "gravatar_id": "",
                   "url": "https://api.github.com/users/bgalek",
                   "html_url": "https://github.com/bgalek",
                   "followers_url": "https://api.github.com/users/bgalek/followers",
                   "following_url": "https://api.github.com/users/bgalek/following{/other_user}",
                   "gists_url": "https://api.github.com/users/bgalek/gists{/gist_id}",
                   "starred_url": "https://api.github.com/users/bgalek/starred{/owner}{/repo}",
                   "subscriptions_url": "https://api.github.com/users/bgalek/subscriptions",
                   "organizations_url": "https://api.github.com/users/bgalek/orgs",
                   "repos_url": "https://api.github.com/users/bgalek/repos",
                   "events_url": "https://api.github.com/users/bgalek/events{/privacy}",
                   "received_events_url": "https://api.github.com/users/bgalek/received_events",
                   "type": "User",
                   "site_admin": false
                 },
                 "created": false,
                 "deleted": false,
                 "forced": false,
                 "base_ref": null,
                 "compare": "https://github.com/bgalek/gh-events-test/compare/fb22d6ace1fa...bb9203e5ea45",
                 "commits": [
                   {
                     "id": "bb9203e5ea45d7013c68a3c65da027be5428bfea",
                     "tree_id": "5af836f5fda21075b4a68b42ac001091c9728e5b",
                     "distinct": true,
                     "message": "fix",
                     "timestamp": "2023-02-17T14:20:26+01:00",
                     "url": "https://github.com/bgalek/gh-events-test/commit/bb9203e5ea45d7013c68a3c65da027be5428bfea",
                     "author": {
                       "name": "Bartosz Gałek",
                       "email": "bartosz@email.local",
                       "username": "bgalek"
                     },
                     "committer": {
                       "name": "GitHub",
                       "email": "noreply@github.com",
                       "username": "web-flow"
                     },
                     "added": [
               
                     ],
                     "removed": [
               
                     ],
                     "modified": [
                       "wow.txt"
                     ]
                   }
                 ],
                 "head_commit": {
                   "id": "bb9203e5ea45d7013c68a3c65da027be5428bfea",
                   "tree_id": "5af836f5fda21075b4a68b42ac001091c9728e5b",
                   "distinct": true,
                   "message": "fix",
                   "timestamp": "2023-02-17T14:20:26+01:00",
                   "url": "https://github.com/bgalek/gh-events-test/commit/bb9203e5ea45d7013c68a3c65da027be5428bfea",
                   "author": {
                     "name": "Bartosz Gałek",
                     "email": "bartosz@email.local",
                     "username": "bgalek"
                   },
                   "committer": {
                     "name": "GitHub",
                     "email": "noreply@github.com",
                     "username": "web-flow"
                   },
                   "added": [
               
                   ],
                   "removed": [
               
                   ],
                   "modified": [
                     "wow.txt"
                   ]
                 }
               }
            """;
}
