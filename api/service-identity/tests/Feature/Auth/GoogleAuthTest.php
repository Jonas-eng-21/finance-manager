<?php

namespace Tests\Feature\Auth;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Socialite\Facades\Socialite;
use Laravel\Socialite\Two\User as SocialiteUser;
use Tests\TestCase;
use Mockery;

class GoogleAuthTest extends TestCase
{
    use RefreshDatabase;

    public function test_it_redirects_to_google(): void
    {
        $response = $this->get('/api/auth/google/redirect');

        $response->assertStatus(302);

        $this->assertStringContainsString('accounts.google.com/o/oauth2/auth', $response->headers->get('Location'));
    }

    public function test_it_handles_google_callback_and_returns_jwt(): void
    {
        $abstractUser = Mockery::mock(SocialiteUser::class);
        $abstractUser->shouldReceive('getId')
            ->andReturn('google_id_123456');
        $abstractUser->shouldReceive('getName')
            ->andReturn('Jonas Google');
        $abstractUser->shouldReceive('getEmail')
            ->andReturn('jonas.google@example.com');


        Socialite::shouldReceive('driver->stateless->user')
            ->andReturn($abstractUser);

        $response = $this->getJson('/api/auth/google/callback');

        $response->assertStatus(200)
            ->assertJsonStructure(['token']);

        $this->assertDatabaseHas('users', [
            'email' => 'jonas.google@example.com',
            'name' => 'Jonas Google',
        ]);
    }
}
