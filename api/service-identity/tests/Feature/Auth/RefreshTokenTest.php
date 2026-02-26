<?php

namespace Tests\Feature\Auth;

use App\Infrastructure\Persistence\Eloquent\UserModel;
use App\Infrastructure\Persistence\Eloquent\RefreshTokenModel;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Str;
use Tests\TestCase;

class RefreshTokenTest extends TestCase
{
    use RefreshDatabase;

    private UserModel $user;
    private string $plainToken;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = UserModel::create([
            'name' => 'Jonas Sousa',
            'email' => 'jonas@example.com',
            'password' => password_hash('StrongPassword123!', PASSWORD_BCRYPT),
            'birth_date' => '1990-01-01',
        ]);

        $this->plainToken = Str::random(64);

        RefreshTokenModel::create([
            'id' => Str::uuid()->toString(),
            'user_id' => $this->user->id,
            'token_hash' => hash('sha256', $this->plainToken),
            'expires_at' => now()->addDays(7),
        ]);
    }

    public function test_it_should_refresh_token_successfully_and_revoke_the_old_one(): void
    {
        $response = $this->postJson('/api/refresh', [
            'refresh_token' => $this->plainToken
        ]);

        $response->assertStatus(200)
            ->assertJsonStructure([
                'access_token',
                'refresh_token',
                'token_type',
                'expires_in'
            ]);

        $oldToken = RefreshTokenModel::where('token_hash', hash('sha256', $this->plainToken))->first();
        $this->assertNotNull($oldToken->revoked_at);
    }

    public function test_it_should_return_401_with_invalid_refresh_token(): void
    {
        $response = $this->postJson('/api/refresh', [
            'refresh_token' => 'fake_or_manipulated_token_string'
        ]);

        $response->assertStatus(401)
            ->assertJsonFragment(['error' => __('identity.auth.errors.invalid_refresh_token')]);
    }

    public function test_it_should_return_401_with_expired_refresh_token(): void
    {
        $expiredToken = Str::random(64);

        RefreshTokenModel::create([
            'id' => Str::uuid()->toString(),
            'user_id' => $this->user->id,
            'token_hash' => hash('sha256', $expiredToken),
            'expires_at' => now()->subDay(),
        ]);

        $response = $this->postJson('/api/refresh', [
            'refresh_token' => $expiredToken
        ]);

        $response->assertStatus(401);
    }

    public function test_it_should_return_401_with_revoked_refresh_token(): void
    {
        $revokedToken = Str::random(64);

        RefreshTokenModel::create([
            'id' => Str::uuid()->toString(),
            'user_id' => $this->user->id,
            'token_hash' => hash('sha256', $revokedToken),
            'expires_at' => now()->addDays(7),
            'revoked_at' => now(),
        ]);

        $response = $this->postJson('/api/refresh', [
            'refresh_token' => $revokedToken
        ]);

        $response->assertStatus(401);
    }
}
