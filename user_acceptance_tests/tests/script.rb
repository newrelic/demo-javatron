require 'json'
require 'rest-client'

def get_test_input(file_path)
    test_input = File.read(file_path)
    return test_input
end

def get_service_url_json(test_input)
    service_url = JSON.parse(test_input)
        .fetch('services', [{}])
        .first
        .fetch('urls', [])
        .first
    if service_url.nil?
        puts "Test input does not contain a url for testing"
        puts "Test input: \n#{test_input}"
        exit(1)
    else
        return service_url
    end
end

def get_service_url_regex(test_input)
    service_url = test_input.scan(/testing_endpoint:'(.+)'/)[0][0]

    if service_url.nil?
        puts "Test input does not contain a url for testing"
        puts "Test input: \n#{test_input}"
        exit(1)
    else
        return service_url
    end
end

# service_url will be a full http url including port number.
def run_tests(service_url)
    responses = [
        RestClient.get("#{service_url}/api/inventory"),
        RestClient.get("#{service_url}/api/inventory/1"),
        RestClient.get("#{service_url}/api/validateMessage"),
        RestClient.get("#{service_url}/api/behaviors")
    ]

    responses.each do |response|
        puts "Request: #{response.request.url}, Status: #{response.code}"
    end

    if responses.any? { |response| response.code != 201 }
        puts "A test failed"
        exit(1)
    end
end

def run()
    test_input_location = ENV['TEST_INPUT_FILE_LOCATION']

    test_input = get_test_input()
    service_url = get_service_url_regex(test_input)
    run_tests(service_url)
end

run()
